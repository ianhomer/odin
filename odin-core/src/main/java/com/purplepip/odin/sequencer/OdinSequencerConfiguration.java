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

import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.clock.beats.BeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.operation.OperationReceiver;

/**
 * Odin Sequencer configuration.
 */
public interface OdinSequencerConfiguration {
  BeatsPerMinute getBeatsPerMinute();

  MeasureProvider getMeasureProvider();

  MetricRegistry getMetrics();

  /**
   * The operation receiver is how the sequencer sends operations to external devices.
   *
   * @return operation receiver
   */
  OperationReceiver getOperationReceiver();

  /**
   * The operation transmitter is how the sequence receives operations in from external devices.
   *
   * @return operation transmitter
   */
  /*
   * TODO : Should receiver and transmitter be renamed to be clearer, e.g. output and input.
   * Receiver and transmitter is a little unclear since in each flow there is always a receiver
   * and a transmitter depending on whether you're consider from inside or outside.
   */
  OperationTransmitter getOperationTransmitter();

  /**
   * System microsecond position provider.
   *
   * @return system microsecond position provider
   */
  MicrosecondPositionProvider getMicrosecondPositionProvider();

  FlowFactory getFlowFactory();

  default boolean isStrictEventOrder() {
    return false;
  }

  TriggerFactory getTriggerFactory();

  ActionFactory getActionFactory();

  long getClockStartRoundingFactor();

  /**
   * Get clock start offset.  This is can be used as a count in period.  Technically it can
   * give time for the processors to start and determine first events before the clock starts.
   *
   * @return clock start offset
   */
  long getClockStartOffset();

  /**
   * Get maximum time in ms that components can look forward to pre-calculate events.
   *
   * @return max look forward
   */
  long getMaxLookForward();

  /**
   * Track processor refresh period in milliseconds.
   *
   * @return track processor refresh period in milliseconds
   */
  long getTrackProcessorRefreshPeriod();

  /**
   * Track processor max notes per buffer.
   *
   * @return track processor max notes per buffer
   */
  int getTrackProcessorMaxNotesPerBuffer();

  /**
   * Operation processor refresh period in milliseconds.
   *
   * @return operation processor refresh period in milliseconds.
   */
  long getOperationProcessorRefreshPeriod();

}
