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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to generate flow object for given sequence.
 */
@Slf4j
public class FlowFactory<A> {
  private FlowConfiguration flowConfiguration;
  private SequenceFactory sequenceFactory = new SequenceFactory();

  public FlowFactory(FlowConfiguration flowConfiguration) {
    this.flowConfiguration = flowConfiguration;
  }

  /**
   * Create flow object for the given sequence.
   *
   * @param sequence sequence
   * @return flow
   */
  @SuppressWarnings("unchecked")
  public MutableFlow<Sequence, A> createFlow(Sequence sequence, Clock clock,
                                             MeasureProvider measureProvider) {

    Class<? extends MutableFlow> flowClass = sequenceFactory.getFlowClass(sequence.getFlowName());
    if (flowClass == null) {
      throw new OdinRuntimeException("Flow class " + sequence.getFlowName() + " not registered");
    }
    MutableFlow<Sequence, A> flow;
    try {
      flow = flowClass.getConstructor(Clock.class, MeasureProvider.class)
          .newInstance(clock, measureProvider);
    } catch (InstantiationException | IllegalAccessException
        | NoSuchMethodException | InvocationTargetException e) {
      throw new OdinRuntimeException("Cannot create instance of " + flowClass, e);
    }
    flow.setSequence(copyFrom(sequence));
    flow.setConfiguration(flowConfiguration);
    flow.afterPropertiesSet();

    return flow;
  }

  /**
   * Set sequence in the flow with a copy or a down cast to the required sequence type.
   *
   * @param sequence sequence to use as a template for the one that is set
   */
  public Sequence copyFrom(Sequence sequence) {
    if (sequenceFactory.getFlowClass(sequence.getFlowName()) == null) {
      /*
       * If flow was not defined then we have to resort to a straight sequence copy
       */
      return sequence.copy();
    }
    Class<? extends Sequence> expectedType =
        sequenceFactory.getSequenceClass(sequence.getFlowName());
    Class<? extends MutableSequence> newClassType =
        sequenceFactory.getDefaultSequenceClass(sequence.getFlowName());
    return sequenceFactory.createCopy(expectedType, newClassType, sequence);
  }

  /**
   * For test cases where timing is important it may be necessary to warm the factory up
   * so the first time it is used performance does not cause inconsistencies.  This warm up
   * time is pretty small (around 20ms on a dev machine), but enough to throw a sequencer
   * test that is expecting sequence to start immediately.
   */
  public void warmUp() {
    sequenceFactory.getSequenceNames().forEach(name -> {
      MutableSequence sequence = new GenericSequence();
      sequence.setFlowName(name);
      copyFrom(sequence);
    });
  }
}
