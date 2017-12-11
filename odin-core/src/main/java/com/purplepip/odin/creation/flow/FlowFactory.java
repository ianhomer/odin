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

package com.purplepip.odin.creation.flow;

import com.purplepip.odin.clock.Clock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.sequence.SequenceFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to create flows.
 *
 * @param <A> type of object delivered by the flow.
 */
@Slf4j
public class FlowFactory<A> {
  private SequenceFactory<A> sequenceFactory;
  private FlowConfiguration flowConfiguration;

  public FlowFactory(SequenceFactory<A> sequenceFactory, FlowConfiguration flowConfiguration) {
    this.sequenceFactory = sequenceFactory;
    this.flowConfiguration = flowConfiguration;
  }

  /**
   * Create flow object for the given sequence.
   *
   * @param sequence sequence
   * @param clock clock
   * @param measureProvider measure provider
   * @return flow
   */
  public MutableFlow<Sequence<A>, A> createFlow(
      SequenceConfiguration sequence, Clock clock, MeasureProvider measureProvider) {
    LOG.debug("Creating flow for sequence {}", sequence.getName());
    MutableFlow<Sequence<A>, A> flow = new DefaultFlow<>(clock, measureProvider);
    flow.setSequence(sequenceFactory.newInstance(sequence));
    flow.setConfiguration(flowConfiguration);
    return flow;
  }

  /**
   * Refresh the sequence in the flow.
   *
   * @param flow flow to refresh
   * @param sequence sequence to update flow with
   */
  public void refreshSequence(MutableFlow<Sequence<A>, A> flow, SequenceConfiguration sequence) {
    if (!sequence.equals(flow.getSequence())) {
      if (sequence instanceof Sequence) {
        LOG.debug("Resetting sequence {} in flow", sequence.getName());
        flow.setSequence(sequenceFactory.cast(sequence));
      } else {
        LOG.debug("Recreating sequence {} in flow", sequence.getName());
        flow.setSequence(sequenceFactory.newInstance(sequence));
      }
    } else {
      LOG.debug("Sequence {} not changed in flow", sequence.getName());
    }
  }
}
