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

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.flow.DefaultFlow;
import com.purplepip.odin.sequence.flow.FlowConfiguration;
import com.purplepip.odin.sequence.flow.MutableFlow;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.specificity.AbstractSpecificThingFactory;
import com.purplepip.odin.specificity.ThingConfiguration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to create sequences.
 */
@Slf4j
public class SequenceFactory<A> extends AbstractSpecificThingFactory<Sequence<A>> {
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
    super(classes);
    this.flowConfiguration = flowConfiguration;
  }

  /**
   * Create a copy of the given sequence configuration and cast to the required sequence type.
   *
   * @param sequence sequence to use as a template for the one that is set
   */
  public Sequence<A> newSequence(SequenceConfiguration sequence) {
    Class<? extends Sequence<A>> expectedType = getClass(sequence.getFlowName());
    return newInstance(sequence, expectedType);
  }

  @Override
  protected void populate(Sequence<A> destination, ThingConfiguration source) {
    if (destination instanceof MutableSequenceConfiguration
        && source instanceof SequenceConfiguration) {
      // TODO : BeanCopy doesn't seem to copy list of layers so we'll do this manually
      destination.getLayers().addAll(((SequenceConfiguration) source).getLayers());
    }
    super.populate(destination, source);
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

  /**
   * For test cases where timing is important it may be necessary to warm the factory up
   * so the first time it is used performance does not cause inconsistencies.  This warm up
   * time is pretty small (around 20ms on a dev machine), but enough to throw a sequencer
   * test that is expecting sequence to start immediately.
   */
  private void warmUp() {
    getNames().forEach(name -> {
      MutableSequenceConfiguration sequence = new GenericSequence();
      sequence.setFlowName(name);
      newSequence(sequence);
    });
  }
}
