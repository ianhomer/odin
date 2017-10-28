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

package com.purplepip.odin.sequence;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.flow.DefaultFlow;
import com.purplepip.odin.sequence.flow.FlowConfiguration;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.flow.MutableFlow;
import com.purplepip.odin.sequence.flow.RationalTypeConverter;
import com.purplepip.odin.sequence.flow.RealTypeConverter;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class SequenceFactory<A> {
  private final Map<String, Class<? extends Sequence<A>>> sequences = new HashMap<>();
  private FlowConfiguration flowConfiguration;

  /**
   * Create the note sequence factory.
   *
   * @return a new note sequence factory
   */
  public static SequenceFactory<Note> createNoteSequenceFactory(
      FlowConfiguration flowConfiguration) {
    /*
     * Coded registration of known sequences.  In the future we may design a plugin architecture,
     * but for now it is kept tight by only allowing registered classes.
     */
    List<Class<? extends Sequence<Note>>> classes = new ArrayList<>();
    classes.add(Metronome.class);
    classes.add(Notation.class);
    classes.add(Pattern.class);
    SequenceFactory<Note> sequenceFactory = new SequenceFactory<>(flowConfiguration, classes);
    sequenceFactory.warmUp();
    return sequenceFactory;
  }

  /**
   * Create a new sequence factory.
   *
   * @param classes sequence classes to initialise with
   */
  public SequenceFactory(FlowConfiguration flowConfiguration,
                         List<Class<? extends Sequence<A>>> classes) {
    this.flowConfiguration = flowConfiguration;
    for (Class<? extends Sequence<A>> clazz : classes) {
      register(clazz);
    }
  }

  private void register(Class<? extends Sequence<A>> clazz) {
    if (clazz.isAnnotationPresent(FlowDefinition.class)) {
      FlowDefinition definition = clazz.getAnnotation(FlowDefinition.class);
      sequences.put(definition.name(), clazz);
    } else {
      Annotation[] annotations = clazz.getAnnotations();
      LOG.warn("Class {} MUST have a @FlowDefinition annotation, it has {}", clazz, annotations);
    }
  }

  static {
    TypeConverterManager.register(Real.class, new RealTypeConverter());
    TypeConverterManager.register(Rational.class, new RationalTypeConverter());
  }

  /**
   * Create a copy of the given sequence configuration and cast to the required sequence type.
   *
   * @param sequence sequence to use as a template for the one that is set
   */
  public Sequence<A> newSequence(SequenceConfiguration sequence) {
    Class<? extends Sequence<A>> expectedType = getSequenceClass(sequence.getFlowName());
    return newSequence(sequence, expectedType);
  }

  /**
   * Create a copy of the sequence with the expected type.
   *
   * @param expectedType expected type of the returned sequence
   * @param original     original sequence to copy values from
   * @return sequence of expected type
   */
  @SuppressWarnings("unchecked")
  <S extends Sequence<A>> S newSequence(SequenceConfiguration original,
                                               Class<? extends S> expectedType) {
    S newSequence;
    if (expectedType == null) {
      throw new OdinRuntimeException("Expected sequence type for " + original.getFlowName()
          + " is not set");
    } else {
      if (expectedType.isAssignableFrom(original.getClass())) {
        /*
         * If the original is of the correct type then we can simply take a copy
         */
        newSequence = (S) expectedType.cast(original).copy();
        LOG.debug("Starting flow with sequence copy {}", newSequence);
      } else {
        LOG.debug("Creating new instance of {}", expectedType);
        try {
          newSequence = expectedType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new OdinRuntimeException("Cannot create new instance of " + expectedType, e);
        }
        final MutableSequenceConfiguration mutableSequence =
            (MutableSequenceConfiguration) newSequence;
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
        newSequence.afterPropertiesSet();
        LOG.debug("Starting flow with typed copy {}", newSequence);
      }
    }
    return newSequence;
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
    flow.setSequence(newSequence(sequence));
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
    flow.setSequence(newSequence(sequence));
  }

  public Class<? extends Sequence<A>> getSequenceClass(String name) {
    return sequences.get(name);
  }

  public Stream<String> getSequenceNames() {
    return sequences.keySet().stream();
  }

  /**
   * For test cases where timing is important it may be necessary to warm the factory up
   * so the first time it is used performance does not cause inconsistencies.  This warm up
   * time is pretty small (around 20ms on a dev machine), but enough to throw a sequencer
   * test that is expecting sequence to start immediately.
   */
  private void warmUp() {
    getSequenceNames().forEach(name -> {
      MutableSequenceConfiguration sequence = new GenericSequence();
      sequence.setFlowName(name);
      newSequence(sequence);
    });
  }
}
