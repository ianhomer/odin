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

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.flow.FailOverFlow;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.NotationFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import jodd.bean.BeanCopy;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import jodd.typeconverter.TypeConverterManager;
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

  /*
   * Statically register known flows.  In the future we may design a plugin architecture, but
   * for now it is kept tight by only allowing registered classes.
   */

  static {
    register(MetronomeFlow.class);
    register(NotationFlow.class);
    register(PatternFlow.class);

    TypeConverterManager.register(Real.class, new RealTypeConverter());
    TypeConverterManager.register(Rational.class, new RationalTypeConverter());
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
    Class<? extends MutableFlow<Sequence, A>> flowClass;
    flowClass = (Class<? extends MutableFlow<Sequence, A>>)
          FLOWS.get(sequence.getFlowName());
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
    setSequence(flow, sequence);
    flow.setClock(clock);
    flow.setMeasureProvider(measureProvider);
    flow.setConfiguration(flowConfiguration);
    flow.afterPropertiesSet();

    return flow;
  }

  /**
   * Set sequence in the flow with a copy or a down cast to the required sequence type.
   *
   * @param flow flow to set the sequence in
   * @param sequence sequence to use as a template for the one that is set
   */
  public void setSequence(MutableFlow<Sequence, A> flow, final Sequence sequence) {
    Class<? extends Sequence> expectedSequenceType = SEQUENCES.get(sequence.getFlowName());
    Class<? extends Sequence> defaultSequenceClass = DEFAULT_SEQUENCES.get(sequence.getFlowName());
    Sequence newSequence;
    if (expectedSequenceType == null || defaultSequenceClass == null) {
      if (expectedSequenceType == null) {
        LOG.warn("Expected sequence type for {} is not set", sequence.getFlowName());
      }
      if (defaultSequenceClass == null) {
        LOG.warn("Expected sequence type for {} is not set", sequence.getFlowName());
      }
      /*
       * We'll just create copy then.
       */
      newSequence = sequence.copy();
    } else {
      if (expectedSequenceType.isAssignableFrom(sequence.getClass())) {
        newSequence = sequence.copy();
        LOG.debug("Starting flow with flow copy", newSequence);
      } else {
        try {
          newSequence = defaultSequenceClass.newInstance();
          final MutableSequence mutableSequence = (MutableSequence) newSequence;
          // TODO : BeanCopy doesn't seem to copy list of layers so we'll do this manually
          mutableSequence.getLayers().addAll(sequence.getLayers());
          BeanCopy.from(sequence).to(mutableSequence).copy();
          sequence.getPropertyNames()
              .forEach(name -> {
                mutableSequence.setProperty(name, sequence.getProperty(name));
                try {
                  BeanUtil.declared.setProperty(mutableSequence, name, sequence.getProperty(name));
                } catch (BeanException exception) {
                  LOG.warn("Whilst creating sequence {}", exception.getMessage());
                }
              });
          LOG.debug("Starting flow with flow down case {}", newSequence);
        } catch (InstantiationException | IllegalAccessException e) {
          LOG.error("Cannot create new instance of " + defaultSequenceClass, e);
          newSequence = sequence.copy();
        }
      }
    }
    flow.setSequence(newSequence);
  }
}
