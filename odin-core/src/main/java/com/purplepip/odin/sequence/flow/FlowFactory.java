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

package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.music.flow.FailOverFlow;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.NotationFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultSequence;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to generate flow object for given sequence.
 */
@Slf4j
public class FlowFactory<A> {
  private static final Map<String, Class<? extends MutableFlow>> FLOWS = new HashMap<>();
  private static final Map<String, Class<? extends MutableSequence>>
      DEFAULT_SEQUENCES = new HashMap<>();
  private static final Map<String, Class<? extends Sequence>>
      SEQUENCES = new HashMap<>();
  private static final Class FAIL_OVER_FLOW_CLASS = FailOverFlow.class;

  private FlowConfiguration flowConfiguration;
  private SequenceFactory sequenceFactory = new SequenceFactory();

  /*
   * Statically register known flows.  In the future we may design a plugin architecture, but
   * for now it is kept tight by only allowing registered classes.
   */

  static {
    register(MetronomeFlow.class);
    register(NotationFlow.class);
    register(PatternFlow.class);
  }

  public FlowFactory(FlowConfiguration flowConfiguration) {
    this.flowConfiguration = flowConfiguration;
  }

  @SuppressWarnings("unchecked")
  private static void register(Class<? extends MutableFlow> clazz) {
    if (clazz.isAnnotationPresent(FlowDefinition.class)) {
      FlowDefinition definition = clazz.getAnnotation(FlowDefinition.class);
      FLOWS.put(definition.name(), clazz);
      SEQUENCES.put(definition.name(), definition.sequence());
      DEFAULT_SEQUENCES.put(definition.name(), definition.defaultSequence());
    } else {
      Annotation[] annotations = clazz.getAnnotations();
      LOG.warn("Class {} MUST have a @FlowDefinition annotation, it has {}", clazz, annotations);
    }
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

    Class<? extends MutableFlow> flowClass = FLOWS.get(sequence.getFlowName());
    if (flowClass == null) {
      LOG.error("Flow class " + sequence.getFlowName() + " not registered");
      flowClass = (Class<? extends MutableFlow<Sequence, A>>) FAIL_OVER_FLOW_CLASS;
    }
    MutableFlow<Sequence, A> flow;
    try {
      flow = flowClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      LOG.error("Cannot create instance of " + flowClass, e);
      flow = (MutableFlow<Sequence, A>) new FailOverFlow();
    }
    flow.setSequence(copyFrom(sequence));
    flow.setClock(clock);
    flow.setMeasureProvider(measureProvider);
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
    if (FLOWS.get(sequence.getFlowName()) == null) {
      /*
       * If flow was not defined then we have to resort to a straight sequence copy
       */
      return sequence.copy();
    }
    Class<? extends Sequence> expectedType = SEQUENCES.get(sequence.getFlowName());
    Class<? extends MutableSequence> newClassType = DEFAULT_SEQUENCES.get(sequence.getFlowName());
    return sequenceFactory.createCopy(expectedType, newClassType, sequence);
  }

  /**
   * For test cases where timing is important it may be necessary to warm the factory up
   * so the first time it is used performance does not cause inconsistencies.  First hit of
   * sequence factory create copy can take 20ms or so.
   */
  public void warmUp() {
    FLOWS.keySet().stream().forEach(name -> {
      MutableSequence sequence = new DefaultSequence();
      sequence.setFlowName(name);
      copyFrom(sequence);
    });
  }
}
