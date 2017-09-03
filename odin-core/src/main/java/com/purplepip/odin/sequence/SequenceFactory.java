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

package com.purplepip.odin.sequence;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.flow.FailOverFlow;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.NotationFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.flow.MutableFlow;
import com.purplepip.odin.sequence.flow.RationalTypeConverter;
import com.purplepip.odin.sequence.flow.RealTypeConverter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import jodd.bean.BeanCopy;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import jodd.typeconverter.TypeConverterManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to create sequences.
 */
@Slf4j
public class SequenceFactory {
  private static final Map<String, Class<? extends MutableFlow>> FLOWS = new HashMap<>();
  private static final Map<String, Class<? extends MutableSequence>>
      DEFAULT_SEQUENCES = new HashMap<>();
  private static final Map<String, Class<? extends Sequence>>
      SEQUENCES = new HashMap<>();
  private static final Class<? extends MutableFlow> FAIL_OVER_FLOW_CLASS = FailOverFlow.class;

  /*
   * Statically register known flows.  In the future we may design a plugin architecture, but
   * for now it is kept tight by only allowing registered classes.
   */
  static {
    register(MetronomeFlow.class);
    register(NotationFlow.class);
    register(PatternFlow.class);
  }

  @SuppressWarnings("unchecked")
  private static void register(Class<? extends MutableFlow> clazz) {
    if (clazz.isAnnotationPresent(FlowDefinition.class)) {
      FlowDefinition definition = clazz.getAnnotation(FlowDefinition.class);
      FLOWS.put(definition.name(), clazz);
      try {
        Class<? extends Sequence> sequenceClass = clazz.newInstance().getSequenceClass();
        SEQUENCES.put(definition.name(), sequenceClass);
      } catch (IllegalAccessException | InstantiationException e) {
        LOG.error("Cannot determine sequence interface", e);
      }
      DEFAULT_SEQUENCES.put(definition.name(), definition.sequence());
    } else {
      Annotation[] annotations = clazz.getAnnotations();
      LOG.warn("Class {} MUST have a @FlowDefinition annotation, it has {}", clazz, annotations);
    }
  }

  static {
    TypeConverterManager.register(Real.class, new RealTypeConverter());
    TypeConverterManager.register(Rational.class, new RationalTypeConverter());
  }

  <S extends Sequence> S createTypedCopy(Class<? extends S> newClassType,
                                                S original) {
    return createCopy(newClassType, newClassType, original);
  }

  /**
   * Create a copy of the sequence with the expected type.
   *
   * @param expectedType expected type of the returned sequence
   * @param newClassType class to use for new instance if original not of expected type
   * @param original     original sequence to copy values from
   * @return sequence of expected type
   */
  @SuppressWarnings("unchecked")
  public <S extends Sequence> S createCopy(Class<? extends S> expectedType,
                             Class<? extends S> newClassType,
                             S original) {

    S newSequence;
    if (expectedType == null || newClassType == null) {
      if (expectedType == null) {
        throw new OdinRuntimeException("Expected sequence type for "
            + original.getFlowName() + " is not set");
      }
      throw new OdinRuntimeException("Expected sequence type for " + original.getFlowName()
          + " is not set");
    } else {
      if (expectedType.isAssignableFrom(original.getClass())) {
        /*
         * If the original is of the correct type then we can simply take a copy
         */
        newSequence = (S) original.copy();
        LOG.debug("Starting flow with sequence copy {}", newSequence);
      } else {
        LOG.debug("Creating new instance of {}", newClassType);
        try {
          newSequence = newClassType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new OdinRuntimeException("Cannot create new instance of " + newClassType, e);
        }
        final MutableSequence mutableSequence = (MutableSequence) newSequence;
        // TODO : BeanCopy doesn't seem to copy list of layers so we'll do this manually
        mutableSequence.getLayers().addAll(original.getLayers());
        LOG.debug("Copying original object properties to copy");
        BeanCopy.from(original).to(mutableSequence).copy();
        LOG.debug("Copying original properties map to copy");
        original.getPropertyNames()
            .forEach(name -> {
              mutableSequence.setProperty(name, original.getProperty(name));
              try {
                BeanUtil.declared.setProperty(mutableSequence, name, original.getProperty(name));
              } catch (BeanException e) {
                LOG.debug("Whilst creating sequence {} (full stack)", e);
                LOG.warn("Whilst creating sequence {}", e.getMessage());
              }
            });
        LOG.debug("Starting flow with typed copy {}", newSequence);
      }
    }
    return newSequence;
  }

  public Class<? extends MutableFlow> getFlowClass(String name) {
    return FLOWS.get(name);
  }

  public Class<? extends MutableFlow> getFailOverFlowClass() {
    return FAIL_OVER_FLOW_CLASS;
  }

  public Class<? extends Sequence> getSequenceClass(String name) {
    return SEQUENCES.get(name);
  }

  public Class<? extends MutableSequence> getDefaultSequenceClass(String name) {
    return DEFAULT_SEQUENCES.get(name);
  }

  public Stream<String> getSequenceNames() {
    return FLOWS.keySet().stream();
  }

}
