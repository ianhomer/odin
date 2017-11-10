/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.sequencer;

import com.purplepip.odin.bag.Things;
import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.conductor.LayerConductor;
import com.purplepip.odin.creation.conductor.LayerConductors;
import com.purplepip.odin.creation.conductor.UnmodifiableConductors;
import com.purplepip.odin.creation.reactors.TriggerReactor;
import com.purplepip.odin.creation.reactors.TriggerReactors;
import com.purplepip.odin.creation.track.SequenceTracks;
import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.creation.track.UnmodifiableTracks;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectApplyListener;
import com.purplepip.odin.sequencer.roll.SequenceRollTrack;
import com.purplepip.odin.sequencer.statistics.DefaultOdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.MutableOdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.OdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.UnmodifiableOdinSequencerStatistics;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Core Odin Sequencer.
 */
@Slf4j
public class OdinSequencer implements ProjectApplyListener {
  private OdinSequencerConfiguration configuration;
  private LayerConductors conductors = new LayerConductors();
  private Things<Conductor> immutableConductors =
      new UnmodifiableConductors(conductors);
  private SequenceTracks tracks = new SequenceTracks(immutableConductors);
  private Things<Track> immutableTracks = new UnmodifiableTracks(tracks);
  private TriggerReactors reactors = new TriggerReactors(immutableTracks, immutableConductors);

  private Set<ProgramChangeOperation> programChangeOperations = new HashSet<>();
  private TrackProcessor sequenceProcessor;
  private OperationProcessor operationProcessor;
  private BeatClock clock;
  private boolean started;
  private MutableOdinSequencerStatistics statistics =
      new DefaultOdinSequencerStatistics(
          tracks.getStatistics(), reactors.getStatistics());

  /**
   * Create an odin sequencer.
   *
   * @param configuration configuration for the sequencer
   * @throws OdinException exception
   */
  public OdinSequencer(OdinSequencerConfiguration configuration)
      throws OdinException {
    this.configuration = configuration;
    init();
  }

  /**
   * Initialise the sequencer.
   */
  private void init() {
    clock = new BeatClock(configuration.getBeatsPerMinute(),
        configuration.getMicrosecondPositionProvider(),
        configuration.getClockStartRoundingFactor(),
        configuration.getClockStartOffset());

    /*
     * Create the sequencer receiver.
     */
    ReactorReceiver sequencerReceiver = new ReactorReceiver(reactors,
        configuration.getMetrics());
    configuration.getOperationTransmitter().addListener(sequencerReceiver);

    /*
     * Create the processors early.  Note that they'll start when the clock starts.
     */
    operationProcessor = new DefaultOperationProcessor(
        clock, configuration.getOperationReceiver(), configuration.getMetrics());
    sequenceProcessor = new TrackProcessor(
        clock, immutableTracks, operationProcessor, statistics, configuration.getMetrics());
  }

  public OdinSequencerStatistics getStatistics() {
    return new UnmodifiableOdinSequencerStatistics(statistics);
  }

  @Override
  public void onProjectApply(Project project) {
    refreshTracks(project);
  }

  /**
   * Refresh sequencer trackSet from the project configuration.
   */
  private void refreshTracks(Project project) {
    refreshChannels(project);
    conductors.refresh(
        project.getLayers().stream(),
        () -> new LayerConductor(clock));
    tracks.refresh(
        project.getSequences().stream(),
        () -> new SequenceRollTrack(clock, configuration.getMeasureProvider(),
            configuration.getSequenceFactory()));
    reactors.refresh(
        project.getTriggers().stream(),
        () -> new TriggerReactor(configuration.getTriggerFactory()));

    LOG.debug("Sequencer refreshed {} : {}", statistics, clock);

    /*
     * If processor is running then process one execution immediately so that the
     * refreshed trackSet can take effect.
     */
    if (sequenceProcessor != null && sequenceProcessor.isRunning()) {
      sequenceProcessor.processOnce();
    }
  }

  private void refreshChannels(Project project) {
    for (Channel channel : project.getChannels()) {
      try {
        /*
         * Only send program change operation if it has not already been sent.
         */
        ProgramChangeOperation programChangeOperation = new ProgramChangeOperation(channel);
        if (!programChangeOperations.contains(programChangeOperation)) {
          LOG.debug("Sending channel operation : {}", channel);
          sendProgramChangeOperation(programChangeOperation);
          statistics.incrementProgramChangeCount();
        } else  {
          LOG.debug("Channel operation already sent : {}", channel);
        }
      } catch (OdinException e) {
        LOG.warn("Cannot send operation", e);
      }
    }
  }

  /**
   * Send program change operation.
   *
   * @param programChangeOperation program change operation
   * @throws OdinException exception
   */
  private void sendProgramChangeOperation(ProgramChangeOperation programChangeOperation)
      throws OdinException {
    operationProcessor.send(programChangeOperation, -1);
    /*
     * Remove any previous program changes on this channel, since they are now historic.
     */
    boolean result = programChangeOperations
        .removeIf(o -> o.getChannel() == programChangeOperation.getChannel());
    LOG.debug("Historic program change option removed for operation {} : {}",
        programChangeOperation, result);
    programChangeOperations.add(programChangeOperation);
  }

  /**
   * Start the sequencer.
   */
  public void start() {
    started = true;
    /*
     * Start the clock.
     */
    clock.start();
  }

  public BeatClock getClock() {
    return clock;
  }

  /**
   * Stop the sequencer.
   */
  public void stop() {
    // TODO : When we stop the sequencer we should play out the buffer, since there
    // might be note off operations to complete.
    clock.stop();
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
