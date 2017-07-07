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
import com.purplepip.odin.properties.ObservableProperty;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.MutableSequenceRoll;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceRoll;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.tick.RuntimeTick;
import com.purplepip.odin.sequence.tick.RuntimeTicks;
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
      if (existingTrack.isPresent()) {
        if (existingTrack.get().getSequence().equals(sequence)) {
          LOG.debug("Sequence {} already added", sequence);
        } else {
          statistics.incrementTrackUpdatedCount();
          try {
            setSequenceInTrack(existingTrack.get(), sequence);
          } catch (OdinException e) {
            LOG.error("Cannot add track for " + sequence, e);
          }
        }
      } else {
        statistics.incrementTrackAddedCount();
        try {
          addSequenceTrack(sequence.copy());
        } catch (OdinException e) {
          LOG.error("Cannot add track for " + sequence, e);
        }
      }
    }
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

  private void addSequenceTrack(Sequence sequence) throws OdinException {
    MutableSequenceRoll<Note> sequenceRoll =
        new MutableSequenceRoll<>(clock, configuration.getMeasureProvider());

    setSequenceInRuntime(sequenceRoll, sequence);

    ObservableProperty<RuntimeTick> runtimeTick = new ObservableProperty<>(
        new RuntimeTick(sequence.getTick()));

    TickConverter tickConverter = new DefaultTickConverter(clock,
        runtimeTick, () -> RuntimeTicks.MICROSECOND,
        sequenceRoll.getOffsetProperty()
    );

    tracks.add(new SequenceTrack(runtimeTick, sequenceRoll, tickConverter));
  }

  private void setSequenceInTrack(SequenceTrack track, Sequence sequence) throws OdinException {
    track.getMutableTick().set(new RuntimeTick(sequence.getTick()));
    setSequenceInRuntime(track.getSequenceRoll(), sequence);
  }

  private void setSequenceInRuntime(SequenceRoll sequenceRoll, Sequence sequence)
      throws OdinException {

    /*
     * Only update the flow if the flow name has changed.
     */
    if (sequenceRoll.getSequence() == null
        || !sequence.getFlowName().equals(sequenceRoll.getSequence().getFlowName())) {
      sequenceRoll.setFlow(configuration.getFlowFactory().createFlow(sequence));
    } else {
      sequenceRoll.getFlow().setSequence(sequence);
    }

    sequenceRoll.setSequence(sequence);
    sequenceRoll.refresh();
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
