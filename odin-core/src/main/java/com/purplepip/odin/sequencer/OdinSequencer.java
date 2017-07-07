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
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectApplyListener;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.MutableSequenceRoll;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.statistics.DefaultOdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.MutableOdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.OdinSequencerStatistics;
import com.purplepip.odin.sequencer.statistics.UnmodifiableOdinSequencerStatistics;
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
  private MutableTracks tracks = new MutableTracks();
  private Tracks immutableTracks = new ImmutableTracks(tracks);
  private Set<ProgramChangeOperation> programChangeOperations = new HashSet<>();
  private TrackProcessor sequenceProcessor;
  private OperationProcessor operationProcessor;
  private BeatClock clock;
  private boolean started;
  private MutableOdinSequencerStatistics statistics = new DefaultOdinSequencerStatistics();

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
     * Create the processors early.  Note that they'll start when the clock starts.
     */
    operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
    sequenceProcessor = new TrackProcessor(
        clock, immutableTracks, operationProcessor, statistics);
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
    refreshSequences(project);

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
        }
      } catch (OdinException e) {
        LOG.warn("Cannot send operation", e);
      }
    }
  }

  private void refreshSequences(Project project) {
    /*
     * Remove any trackSet for which the sequence in the project has been removed.
     */
    int sizeBefore = tracks.size();

    boolean result = tracks.removeIf(track -> project.getSequences().stream()
        .noneMatch(
            sequence -> track instanceof SequenceTrack
                && sequence.getId() == ((SequenceTrack) track).getSequence().getId()
        ));
    if (result) {
      int removalCount = sizeBefore - tracks.size();
      LOG.debug("Removed {} trackSet, ", removalCount);
      statistics.incrementTrackRemovedCount(removalCount);
    } else {
      LOG.debug("No sequence trackSet detected for removal {} / {}", tracks.size(),
          project.getSequences().size());
    }

    for (Sequence sequence : project.getSequences()) {
      /*
       * Add sequence if not present in trackSet.
       */
      Optional<SequenceTrack> existingTrack =
          tracks.stream()
              .filter(o -> o instanceof SequenceTrack)
              .map(o -> (SequenceTrack) o)
              .filter(track -> sequence.getId() == track.getSequence().getId()).findFirst();

      SequenceTrack modifiedTrack = null;
      if (!existingTrack.isPresent()) {
        statistics.incrementTrackAddedCount();
        /*
         * Create new track
         */
        modifiedTrack = createSequenceTrack(sequence);
        tracks.add(modifiedTrack);
      } else {
        if (existingTrack.get().getSequence().equals(sequence)) {
          LOG.debug("Sequence {} already added and unchanged", sequence);
        } else {
          LOG.debug("Updating track for {}", sequence);
          statistics.incrementTrackUpdatedCount();
          modifiedTrack = existingTrack.get();
        }
      }

      if (modifiedTrack != null) {
        // TODO : Why do we need to set the mutable tick on the track?  It should be hooked
        // into sequence on sequence roll.
        modifiedTrack.getMutableTick().set(sequence.getTick());
        /*
         * Update sequence in new or modified track.
         */
        modifiedTrack.getSequenceRoll().setSequence(sequence.copy());
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

  private SequenceTrack createSequenceTrack(Sequence sequence) {
    // TODO : Remove sequence from this method arguments since it's not logically needed and
    // is only included to prevent a NPE.
    MutableSequenceRoll<Note> roll =
        new MutableSequenceRoll<>(clock, configuration.getFlowFactory(),
            configuration.getMeasureProvider());
    return new SequenceTrack(clock, roll, sequence);
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
    clock.stop();
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
