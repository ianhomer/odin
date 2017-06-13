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
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.ProjectListener;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.RuntimeTicks;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SeriesTimeUnitConverterFactory;
import com.purplepip.odin.sequence.flow.Flow;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Core Odin Sequencer.
 */
@Slf4j
public class OdinSequencer implements ProjectListener {
  private OdinSequencerConfiguration configuration;
  private Set<SequenceTrack> sequenceTracks = new HashSet<>();
  private SequenceProcessor sequenceProcessor;
  private OperationProcessor operationProcessor;
  private Clock clock;
  private boolean started;

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

  public ProjectContainer getProjectContainer() {
    return configuration.getProjectContainer();
  }

  /**
   * Initialise the sequencer.
   */
  private void init() {
    configuration.getProjectContainer().addListener(this);
    clock = new Clock(configuration.getBeatsPerMinute(),
        configuration.getMicrosecondPositionProvider(),
        configuration.getClockStartRoundingFactor(),
        configuration.getClockStartOffset());
  }

  @Override
  public void onProjectApply() {
    refreshTracks();
  }

  /**
   * Refresh sequencer tracks from the project configuration.
   */
  private void refreshTracks() {
    LOG.debug("Refreshing tracks at {}micros", clock.getMicrosecondPosition());
    for (Channel channel : getProjectContainer().getChannels()) {
      try {
        LOG.debug("Sending channel operation : {}", channel);
        operationProcessor.send(new ProgramChangeOperation(channel), -1);
      } catch (OdinException e) {
        LOG.warn("Cannot send operation", e);
      }
    }

    sequenceTracks.clear();
    for (Sequence sequence : getProjectContainer().getSequences()) {
      addSequenceTrack(sequence);
    }
  }

  private void addSequenceTrack(Sequence sequence) {
    DefaultSequenceRuntime sequenceRuntime;
    try {
      sequenceRuntime = createSequenceRuntime(
          configuration.getFlowFactory().createFlow(sequence));
    } catch (OdinException e) {
      LOG.error("Cannot add sequence", e);
      return;
    }
    sequenceTracks.add(new SequenceTrack(new SeriesTimeUnitConverterFactory(
        new DefaultTickConverter(clock, sequenceRuntime.getTick(), RuntimeTicks.MICROSECOND,
            sequence.getOffset()))
        .convertSeries(sequenceRuntime), sequence.getChannel()));
  }

  /**
   * Create sequence runtime from the given flow.
   *
   * @param flow flow
   * @return sequence runtime
   */
  private DefaultSequenceRuntime
      createSequenceRuntime(Flow<Sequence, Note> flow) {
    DefaultSequenceRuntime sequenceRuntime = new DefaultSequenceRuntime(flow);
    sequenceRuntime.setSequence(flow.getSequence());
    sequenceRuntime.setMeasureProvider(configuration.getMeasureProvider());
    return sequenceRuntime;
  }

  /**
   * Start the sequencer.
   */
  public void start() {
    started = true;
    /*
     * Create the processors and start the clock.
     */
    operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
    sequenceProcessor = new SequenceProcessor(clock, sequenceTracks, operationProcessor);
    configuration.getProjectContainer().apply();
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
    if (sequenceProcessor != null) {
      sequenceProcessor.close();
    }
    if (operationProcessor != null) {
      operationProcessor.close();
    }
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
