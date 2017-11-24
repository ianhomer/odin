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

import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;

import com.purplepip.odin.clock.DefaultMicrosecondPositionProvider;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.flow.DefaultFlowConfiguration;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSequencerEnvironment {
  private PerformanceContainer container = new PerformanceContainer(new TransientPerformance());

  private OdinSequencerConfiguration configuration;
  private OdinSequencer sequencer;

  private FlowFactory<Note> flowFactory;

  public TestSequencerEnvironment(OperationReceiver operationReceiver) throws OdinException {
    initialiseSequencer(operationReceiver);
  }

  private void initialiseSequencer(OperationReceiver operationReceiver) throws OdinException {
    DefaultFlowConfiguration flowConfiguration = new DefaultFlowConfiguration();
    flowConfiguration.setMaxForwardScan(1000000);


    flowFactory = newNoteFlowFactory(flowConfiguration);
    configuration = new DefaultOdinSequencerConfiguration()
        .setFlowFactory(flowFactory)
        .setMeasureProvider(new StaticBeatMeasureProvider(4))
        .setBeatsPerMinute(new StaticBeatsPerMinute(60000))
        .setClockStartOffset(20000)
        .setClockStartRoundingFactor(1000)
        .setTrackProcessorRefreshPeriod(10)
        .setOperationProcessorRefreshPeriod(10)
        .setMicrosecondPositionProvider(new DefaultMicrosecondPositionProvider())
        .setOperationReceiver(operationReceiver);
    sequencer = new OdinSequencer(configuration);
    container.addApplyListener(sequencer);
  }

  public OdinSequencer getSequencer() {
    return sequencer;
  }

  public PerformanceContainer getContainer() {
    return container;
  }

  public OdinSequencerConfiguration getConfiguration() {
    return configuration;
  }

  public FlowFactory<Note> getFlowFactory() {
    return flowFactory;
  }

  /**
   * Start the environment.
   */
  public void start() {
    LOG.debug("Test environment starting");
    container.apply();
    sequencer.start();
    LOG.debug("... test environment started");
  }

  /**
   * Stop the environment.
   */
  public void stop() {
    sequencer.stop();
  }

}
