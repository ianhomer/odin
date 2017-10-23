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

package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceConfiguration;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to generate flow object for given sequence.
 */
@Slf4j
public class FlowFactory<A> {
  private FlowConfiguration flowConfiguration;
  private SequenceFactory<A> sequenceFactory;

  public FlowFactory(FlowConfiguration flowConfiguration, SequenceFactory<A> sequenceFactory) {
    this.flowConfiguration = flowConfiguration;
    this.sequenceFactory = sequenceFactory;
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
    MutableFlow<Sequence<A>, A> flow = new DefaultFlow<>(clock, measureProvider);
    flow.setSequence(sequenceFactory.newSequence(sequence));
    flow.setConfiguration(flowConfiguration);
    flow.afterPropertiesSet();
    return flow;
  }

  /**
   * Refresh the sequence in the flow.
   *
   * @param flow flow to refresh
   * @param sequence sequence to update flow with
   */
  public void refreshSequence(MutableFlow<Sequence<A>, A> flow, SequenceConfiguration sequence) {
    flow.setSequence(sequenceFactory.newSequence(sequence));
  }
}
