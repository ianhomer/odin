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

package com.purplepip.odin.server;

import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.MidiSystemWrapper;
import com.purplepip.odin.operation.OperationReceiver;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.PerformanceLoader;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring Boot of Odin.
 */
@Configuration
public class OdinConfiguration {
  private static final int FOUR_FOUR_TIME = 4;

  @Autowired(required = false)
  private OperationReceiver auditingOperationReceiver;

  @Autowired(required = false)
  private MetricRegistry metrics;

  @Autowired(required = false)
  private PerformanceLoader performanceLoader;

  @Bean
  public MidiDeviceWrapper midiDeviceWrapper() throws OdinException {
    return new MidiDeviceWrapper();
  }

  @Bean
  public MeasureProvider measureProvider() {
    return new StaticBeatMeasureProvider(FOUR_FOUR_TIME);
  }

  @Bean
  public MidiSystemWrapper midiSystemWrapper() {
    return new MidiSystemWrapper();
  }

  /**
   * Create Odin sequencer.
   *
   * @param measureProvider   measure provider
   * @param midiDeviceWrapper MIDI device wrapper
   * @param performanceContainer  performance container
   * @return Odin sequencer
   * @throws OdinException exception
   */
  @Bean
  public OdinSequencer odinSequencer(MeasureProvider measureProvider,
                                     MidiDeviceWrapper midiDeviceWrapper,
                                     PerformanceContainer performanceContainer)
      throws OdinException {
    List<OperationReceiver> operationReceivers = new ArrayList<>();
    operationReceivers.add(new MidiOperationReceiver(midiDeviceWrapper));
    if (performanceLoader != null) {
      operationReceivers.add(performanceLoader);
    }
    if (auditingOperationReceiver != null) {
      operationReceivers.add(auditingOperationReceiver);
    }
    DefaultOdinSequencerConfiguration configuration = new DefaultOdinSequencerConfiguration()
        .setBeatsPerMinute(new StaticBeatsPerMinute(120))
        .setMeasureProvider(measureProvider)
        .setOperationReceiver(new OperationReceiverCollection(operationReceivers))
        .setMicrosecondPositionProvider(
            new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper.getReceivingDevice()));

    /*
     * Metrics is optional, e.g. not needed for most tests.  However even when not set
     * metrics is enabled internally, just not using the spring loaded bean.
     */
    if (metrics != null) {
      configuration.setMetrics(metrics);
    }

    OdinSequencer odinSequencer = new OdinSequencer(configuration);
    performanceContainer.addApplyListener(odinSequencer);
    return odinSequencer;
  }
}
