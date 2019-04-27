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

import static com.purplepip.odin.configuration.ActionFactories.newActionFactory;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static com.purplepip.odin.system.TestContainer.factorSpeed;
import static com.purplepip.odin.system.TestContainer.factorTime;

import com.purplepip.odin.clock.DefaultMicrosecondPositionProvider;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSequencerEnvironment {
  private PerformanceContainer container;

  private OdinSequencerConfiguration configuration;
  private OdinSequencer sequencer;
  private OdinSequencerConfiguration deltaConfiguration;
  private boolean prepared = false;

  private FlowFactory flowFactory;

  public TestSequencerEnvironment(OperationHandler operationReceiver) {
    this(operationReceiver, new TransientPerformance());
  }

  public TestSequencerEnvironment(OperationHandler operationReceiver,
                                  Performance performance) {
    this(operationReceiver, performance, new DeltaOdinSequencerConfiguration());
  }

  public TestSequencerEnvironment(OperationHandler operationReceiver,
                                  Performance performance,
                                  OdinSequencerConfiguration configuration) {
    this(operationReceiver, new DefaultPerformanceContainer(performance), configuration);
  }

  public TestSequencerEnvironment(OperationHandler operationReceiver,
                                  DefaultPerformanceContainer container) {
    this(operationReceiver, container, new DeltaOdinSequencerConfiguration());
  }

  /**
   * Create test sequencer environment.
   *
   * @param operationReceiver operation receiver
   * @param container performance container
   * @param configuration configuration
   */
  public TestSequencerEnvironment(OperationHandler operationReceiver,
                                  DefaultPerformanceContainer container,
                                  OdinSequencerConfiguration configuration) {
    this.container = container;
    this.deltaConfiguration = configuration;
    initialiseSequencer(operationReceiver);
  }

  private void initialiseSequencer(OperationHandler operationReceiver) {
    flowFactory = newNoteFlowFactory();
    configuration = new DefaultOdinSequencerConfiguration()
        .setFlowFactory(flowFactory)
        .setActionFactory(newActionFactory())
        .setMeasureProvider(new StaticBeatMeasureProvider(4))
        .setBeatsPerMinute(new StaticBeatsPerMinute(factorSpeed(6000)))
        .setClockStartOffset(factorTime(50000))
        .setClockStartRoundingFactor(1000)
        .setMaxLookForward(1_000)
        .setTrackProcessorRefreshPeriod(10)
        .setTrackProcessorMaxNotesPerBuffer(2000)
        .setOperationProcessorRefreshPeriod(50)
        .setMicrosecondPositionProvider(new DefaultMicrosecondPositionProvider())
        .setOperationReceiver(operationReceiver)
        .setStrictEventOrder(true).merge(deltaConfiguration);
    LOG.debug("Max look forward : {}", configuration.getMaxLookForward());
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

  public FlowFactory getFlowFactory() {
    return flowFactory;
  }

  /**
   * Prepare the environment prior to starting.
   */
  public void prepare() {
    container.apply();
    sequencer.prepare();
    prepared = true;
  }

  /**
   * Start the environment.
   */
  public void start() {
    LOG.debug("Test environment starting");
    if (!prepared) {
      prepare();
    }
    sequencer.start();
    LOG.debug("... test environment started");
  }

  /**
   * Stop the environment.
   */
  public void stop() {
    sequencer.stop();
  }

  public void shutdown() {
    sequencer.shutdown();
    prepared = false;
  }

}
