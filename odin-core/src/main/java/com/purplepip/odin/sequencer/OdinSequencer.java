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
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.conductor.LayerConductor;
import com.purplepip.odin.creation.conductor.LayerConductors;
import com.purplepip.odin.creation.conductor.UnmodifiableConductors;
import com.purplepip.odin.creation.reactors.TriggerReactor;
import com.purplepip.odin.creation.reactors.TriggerReactors;
import com.purplepip.odin.creation.track.SequenceRollTrack;
import com.purplepip.odin.creation.track.SequenceTracks;
import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.creation.track.UnmodifiableTracks;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceApplyListener;
import com.purplepip.odin.sequencer.statistics.DefaultOdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.MutableOdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.OdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.UnmodifiableOdinSequencerStatistics;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Core Odin Sequencer.
 */
@Slf4j
public class OdinSequencer implements PerformanceApplyListener {
  private OdinSequencerConfiguration configuration;
  private LayerConductors conductors = new LayerConductors();
  private Things<Conductor> immutableConductors =
      new UnmodifiableConductors(conductors);
  private SequenceTracks tracks = new SequenceTracks(immutableConductors);
  private Things<Track> immutableTracks = new UnmodifiableTracks(tracks);
  /*
   * Note that reactors can change tracks so we use the mutable tracks for the trigger reactors.
   */
  private TriggerReactors reactors = new TriggerReactors(tracks, immutableConductors);

  private Set<ProgramChangeOperation> programChangeOperations = new LinkedHashSet<>();
  private TrackProcessor trackProcessor;
  private DefaultOperationProcessor operationProcessor;
  private BeatClock clock;
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
    clock = new BeatClock(new BeatClock.Configuration()
        .beatsPerMinute(configuration.getBeatsPerMinute())
        .microsecondPositionProvider(configuration.getMicrosecondPositionProvider())
        .startOffset(configuration.getClockStartOffset())
        .maxLookForwardInMillis(configuration.getMaxLookForward())
        .metrics(configuration.getMetrics()));
    clock.addListener(configuration.getOperationReceiver());

    /*
     * Create the reactor receiver.
     */
    ReactorReceiver reactorReceiver = new ReactorReceiver(reactors,
        configuration.getMetrics(), configuration.getOperationTransmitter());
    configuration.getOperationTransmitter().addListener(reactorReceiver);

    /*
     * Transmit signals from the transmitter onto the operation receiver used by the processor.
     * TODO : Rename these receivers to make clearer one is used by the reactor, one is
     * used by the operation processor.  Passing all operations onto the operation transmitter
     * might be a little expensive since we don't necessarily want all internal operations
     * going back on to the reactor ... one to think about.
     */
    configuration.getOperationTransmitter().addListener(configuration.getOperationReceiver());

    /*
     * Create the processors early.  Note that they'll start when the clock starts.
     */
    operationProcessor = new DefaultOperationProcessor(
        clock, configuration.getOperationReceiver(),
        configuration.getMetrics(),
        configuration.getOperationProcessorRefreshPeriod(),
        configuration.isStrictEventOrder());
    trackProcessor = new TrackProcessor(
        clock, immutableTracks, operationProcessor, statistics,
        configuration.getMetrics(),
        configuration.getTrackProcessorRefreshPeriod(),
        configuration.getTrackProcessorMaxNotesPerBuffer());
  }

  public OdinSequencerStatistics getStatistics() {
    return new UnmodifiableOdinSequencerStatistics(statistics);
  }

  @Override
  public void onPerformanceApply(Performance performance) {
    refreshTracks(performance);
  }

  /**
   * Refresh sequencer trackSet from the performance configuration.
   */
  private void refreshTracks(Performance performance) {
    refreshChannels(performance);
    conductors.refresh(
        performance.getLayers().stream(),
        layer -> new LayerConductor(layer, clock));
    tracks.refresh(
        performance.getSequences().stream(),
        sequence -> new SequenceRollTrack(sequence, clock, configuration.getMeasureProvider(),
            configuration.getFlowFactory()));
    reactors.refresh(
        performance.getTriggers().stream(),
        trigger -> new TriggerReactor(trigger, configuration.getTriggerFactory()));

    LOG.debug("Sequencer refreshed {} : {}", statistics, clock);

    /*
     * If processor is running then process one execution immediately so that the
     * refreshed trackSet can take effect.
     */
    if (trackProcessor != null && trackProcessor.isRunning()) {
      trackProcessor.processOnce();
    }
  }

  private void refreshChannels(Performance performance) {
    for (Channel channel : performance.getChannels()) {
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
    /*
     * Start the clock.
     */
    clock.start();
    /*
     * Process tracks and operations immediately.
     */
    trackProcessor.runOnce();
    operationProcessor.runOnce();
  }

  /**
   * Prepare the sequencer.
   */
  public void prepare() {
    clock.prepare();
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
  }

  /**
   * Shutdown the sequencer.
   */
  public void shutdown() {
    if (clock.isStartingOrStarted()) {
      clock.stop();
    }
    clock.shutdown();
    conductors.clear();
    tracks.clear();
    reactors.clear();
  }

  public boolean isStarted() {
    return clock.isStarted();
  }
}
