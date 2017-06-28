/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectApplyListener;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.RuntimeTicks;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SeriesTimeUnitConverterFactory;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Core Odin Sequencer.
 */
@Slf4j
public class OdinSequencer implements ProjectApplyListener {
  private OdinSequencerConfiguration configuration;
  private Set<SequenceTrack> sequenceTracks = new HashSet<>();
  private Set<ProgramChangeOperation> programChangeOperations = new HashSet<>();
  private SequenceProcessor sequenceProcessor;
  private OperationProcessor operationProcessor;
  private Clock clock;
  private boolean started;
  private MutableOdinSequenceStatistics statistics = new MutableOdinSequenceStatistics();

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
    clock = new Clock(configuration.getBeatsPerMinute(),
        configuration.getMicrosecondPositionProvider(),
        configuration.getClockStartRoundingFactor(),
        configuration.getClockStartOffset());
    /*
     * Create the processors early.  Note that they'll start when the clock starts.
     */
    operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
    sequenceProcessor = new SequenceProcessor(clock, sequenceTracks, operationProcessor);
  }

  public OdinSequenceStatistics getStatistics() {
    return new UnmodifiableOdinSequenceStatistics(statistics);
  }

  @Override
  public void onProjectApply(Project project) {
    refreshTracks(project);
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

  private void refreshChannels(Project project) {
    for (Channel channel : project.getChannels()) {
      try {
        LOG.debug("Sending channel operation : {}", channel);
        /*
         * Only send program change operation if it has not already been sent.
         */
        ProgramChangeOperation programChangeOperation = new ProgramChangeOperation(channel);
        if (!programChangeOperations.contains(programChangeOperation)) {
          sendProgramChangeOperation(programChangeOperation);
        }
      } catch (OdinException e) {
        LOG.warn("Cannot send operation", e);
      }
    }
  }

  private void refreshSequences(Project project) {
    /*
     * Remove any tracks for which the sequence in the project has been removed.
     */
    int sizeBefore = sequenceTracks.size();
    boolean result = sequenceTracks.removeIf(t -> project.getSequences().stream().noneMatch(
        s -> s.getId() == t.getSequenceRuntime().getSequence().getId()
    ));
    if (result) {
      statistics.incrementTrackRemovedCount(sizeBefore - sequenceTracks.size());
    }

    for (Sequence sequence : project.getSequences()) {
      /*
       * Add sequence if not present in tracks.
       */
      Optional<SequenceTrack> existingTrack = sequenceTracks.stream().filter(s ->
          sequence.getId() == s.getSequenceRuntime().getSequence().getId()).findFirst();
      if (existingTrack.isPresent()) {
        if (existingTrack.get().getSequenceRuntime().getSequence().equals(sequence)) {
          LOG.debug("Sequence {} already added", sequence);
        } else {
          /*
           * TODO : Update sequence runtime on the fly instead of delete and recreate
           */
          statistics.incrementTrackUpdatedCount();
          sequenceTracks.remove(existingTrack.get());
          addSequenceTrack(sequence.copy());
        }
      } else {
        statistics.incrementTrackAddedCount();
        addSequenceTrack(sequence.copy());
      }
    }
  }
  
  /**
   * Refresh sequencer tracks from the project configuration.
   */
  private void refreshTracks(Project project) {
    refreshChannels(project);
    refreshSequences(project);

    LOG.debug("Sequencer refreshed {} : {}", statistics, clock);

    /*
     * If processor is running then process one execution immediately so that the
     * refreshed tracks can take effect.
     */
    if (sequenceProcessor != null && sequenceProcessor.isRunning()) {
      sequenceProcessor.processOnce();
    }
  }

  private void addSequenceTrack(Sequence sequence) {
    DefaultSequenceRuntime sequenceRuntime =
        new DefaultSequenceRuntime(clock, configuration.getMeasureProvider());
    setSequenceTrackFlow(sequenceRuntime, sequence);

    sequenceRuntime.setSequence(sequence);
    sequenceRuntime.refresh();

    sequenceTracks.add(
        new SequenceTrack(
            new SeriesTimeUnitConverterFactory(
                new DefaultTickConverter(clock,
                    sequenceRuntime.getTick(), RuntimeTicks.MICROSECOND, sequence.getOffset()
                )
            ).convertSeries(sequenceRuntime)));
  }

  private void setSequenceTrackFlow(DefaultSequenceRuntime sequenceRuntime, Sequence sequence) {
    try {
      sequenceRuntime.setFlow(configuration.getFlowFactory().createFlow(sequence));
    } catch (OdinException e) {
      LOG.error("Cannot set sequence track flow", e);
    }
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

  public Clock getClock() {
    return clock;
  }

  /**
   * Stop the sequencer.
   */
  public void stop() {
    clock.stop();
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
