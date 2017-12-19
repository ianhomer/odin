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

import static com.purplepip.odin.clock.tick.Ticks.BEAT;

import com.google.common.collect.Lists;
import com.purplepip.odin.clock.tick.DefaultTick;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.channel.DefaultChannel;
import com.purplepip.odin.creation.layer.DefaultLayer;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.layer.MutableLayer;
import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.creation.sequence.MutableSequenceConfiguration;
import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.GenericTrigger;
import com.purplepip.odin.creation.triggers.MutableTriggerConfiguration;
import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.Notes;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.properties.beany.Setter;
import com.purplepip.odin.specificity.Specifics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Convenience class for building up sequences.
 */
@Slf4j
public class PerformanceBuilder {
  private static final String DEFAULT_NOTATION_FORMAT = "natural";
  private static final String DEFAULT_FLOW_NAME = "pattern";

  private PerformanceContainer projectContainer;
  private String name;
  private String typeName = DEFAULT_FLOW_NAME;
  private boolean enabled;
  private int channel;
  private int noteNumber;
  private int velocity;
  private Rational length;
  private int offset;
  private Tick tick = BEAT;
  private List<String> layerNamesToAdd = new ArrayList<>();
  private Map<String, Action> triggersToAdd = new HashMap<>();
  private List<Long> sequenceIds = new ArrayList<>();
  private List<Long> channelIds = new ArrayList<>();
  private List<Long> layerIds = new ArrayList<>();
  private List<Long> triggerIds = new ArrayList<>();
  private Map<String, String> properties = new HashMap<>();

  public PerformanceBuilder(PerformanceContainer projectContainer) {
    this.projectContainer = projectContainer;
    reset();
  }

  /**
   * Clear the project builder so that it can be used again.  Note that the underlying project is
   * NOT cleared.
   */
  public final void reset() {
    sequenceIds.clear();
    channelIds.clear();
    layerIds.clear();
    withDefaults();
  }

  public Layer getLayer(String name) {
    return projectContainer.getLayerStream().filter(l -> name.equals(l.getName())).findFirst()
        .orElse(null);
  }

  public TriggerConfiguration getTrigger(String name) {
    return projectContainer.getTriggerStream().filter(l -> name.equals(l.getName())).findFirst()
        .orElse(null);
  }

  /*
   * Get sequence IDs added so that we can inspect the sequences that were created.  Note that
   * this is an ordered list in the order that they were added.  Note that sequences in a project
   * do not have a fixed ordering so we can not rely on the iteration of the project sequences.
   */
  public SequenceConfiguration getSequenceByOrder(int position) {
    return projectContainer.getSequence(sequenceIds.get(position));
  }

  /*
   * As per getSequenceByOrder logic.
   */
  public Channel getChannelByOrder(int position) {
    return projectContainer.getChannel(channelIds.get(position));
  }

  /*
   * As per getSequenceByOrder logic.
   */
  public Layer getLayerByOrder(int position) {
    return projectContainer.getLayer(layerIds.get(position));
  }

  /*
 * As per getSequenceByOrder logic.
 */
  public TriggerConfiguration getTriggerByOrder(int id) {
    return projectContainer.getTrigger(triggerIds.get(id));
  }

  private static MutableSequenceConfiguration withFlow(MutableSequenceConfiguration sequence,
                                                       Class<? extends Sequence> clazz) {
    sequence.setType(Specifics.getName(clazz));
    return sequence;
  }

  private MutableSequenceConfiguration withDefaultsForMetronome(
      MutableSequenceConfiguration sequence) {
    sequence.setTick(createTick(Ticks.HALF));
    withFlow(sequence, Metronome.class);
    new Setter(sequence)
        .set("noteBarStart", createNote(Notes.newNote()))
        .set("noteBarMid", createNote(new DefaultNote(64,Notes.DEFAULT_VELOCITY / 2,
            Notes.DEFAULT_DURATION)));
    return sequence;
  }

  private MutableSequenceConfiguration withDefaultsForPattern(
      MutableSequenceConfiguration sequence) {
    return withFlow(withDefaults(sequence), Pattern.class);
  }

  private MutableSequenceConfiguration withDefaultsForNotation(
      MutableSequenceConfiguration sequence) {
    return withFlow(withDefaults(sequence), Notation.class);
  }

  private MutableSequenceConfiguration withDefaults(MutableSequenceConfiguration sequence) {
    sequence.setTick(withDefaults(createTick(tick)));
    return sequence;
  }

  private static Tick withDefaults(Tick tick) {
    return tick;
  }

  /**
   * Set the internal with variables to defaults.
   *
   * @return this
   */
  public PerformanceBuilder withDefaults() {
    enabled = true;
    name = null;
    channel = 0;
    noteNumber = Notes.DEFAULT_NUMBER;
    velocity = Notes.DEFAULT_VELOCITY;
    length = Wholes.MINUS_ONE;
    offset = 0;
    layerNamesToAdd.clear();
    triggersToAdd.clear();
    return this;
  }

  private MutableLayer withDefaults(MutableLayer layer) {
    layer.setTick(createTick(BEAT));
    layer.setLength(length);
    layer.setOffset(Wholes.valueOf(offset));
    return layer;
  }

  /**
   * Create Layer.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return layer
   */
  protected MutableLayer createLayer() {
    return new DefaultLayer();
  }

  /**
   * Create sequence.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return sequence
   */
  protected MutableTriggerConfiguration createTrigger() {
    return new GenericTrigger();
  }

  /**
   * Create trigger.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return metronome
   */
  protected NoteTrigger createNoteTrigger() {
    return new NoteTrigger();
  }

  /**
   * Create sequence.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return sequence
   */
  protected MutableSequenceConfiguration createSequence() {
    return new GenericSequence();
  }

  /**
   * Create Metronome.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return metronome
   */
  protected MutableSequenceConfiguration createMetronome() {
    return new Metronome();
  }

  /**
   * Create Notation.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return notation
   */
  protected MutableSequenceConfiguration createNotation() {
    return new Notation();
  }

  /**
   * Create Pattern.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return pattern
   */
  protected MutableSequenceConfiguration createPattern() {
    return new Pattern();
  }

  /**
   * Create Note.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @param number note number
   * @param velocity velocity
   * @param duration duration
   * @return note
   */
  protected Note createNote(int number, int velocity, Real duration) {
    return new DefaultNote(number, velocity, duration);
  }

  private Note createNote(Note note) {
    return createNote(note.getNumber(), note.getVelocity(), note.getDuration());
  }

  protected Channel createChannel() {
    return new DefaultChannel();
  }

  protected Tick createTick(Tick tick) {
    if (tick instanceof DefaultTick) {
      return tick;
    } else {
      return new DefaultTick(tick);
    }
  }

  private void addSequenceToContainer(SequenceConfiguration sequence) {
    sequenceIds.add(sequence.getId());
    projectContainer.addSequence(sequence);
  }

  private void addTriggerToContainer(MutableTriggerConfiguration trigger) {
    triggerIds.add(trigger.getId());
    projectContainer.addTrigger(trigger);
  }

  /**
   * Add metronome.
   *
   * @return this sequence builder
   */
  public PerformanceBuilder addMetronome() {
    MutableSequenceConfiguration metronome = withDefaultsForMetronome(createMetronome());
    addSequenceToContainer(applyParameters(metronome));
    return this;
  }

  /**
   * Add layers with the given names.
   *
   * @param names names of layers
   * @return this project builder
   */
  public PerformanceBuilder addLayer(String... names) {
    for (String layerName : names) {
      MutableLayer layer = withDefaults(createLayer());
      layer.setName(layerName);
      applyParameters(layer);
      layerIds.add(layer.getId());
      projectContainer.addLayer(layer);
    }
    return this;
  }

  /**
   * Add simple message trigger.
   *
   * @return this sequence builder
   */
  public PerformanceBuilder addNoteTrigger() {
    NoteTrigger trigger = createNoteTrigger();
    trigger.setNote(new DefaultNote(noteNumber, 0, 0));
    trigger.setType("note");
    addTriggerToContainer(applyParameters(trigger));
    return this;
  }


  public PerformanceBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public PerformanceBuilder withTick(Tick tick) {
    this.tick = tick;
    return this;
  }

  public PerformanceBuilder withFlowName(String typeName) {
    this.typeName = typeName;
    return this;
  }

  public PerformanceBuilder withProperty(String name, String value) {
    properties.put(name, value);
    return this;
  }

  public PerformanceBuilder withProperty(String name, long value) {
    properties.put(name, String.valueOf(value));
    return this;
  }


  public PerformanceBuilder withChannel(int channel) {
    this.channel = channel;
    return this;
  }


  public PerformanceBuilder withNote(int noteNumber) {
    this.noteNumber = noteNumber;
    return this;
  }

  public PerformanceBuilder withVelocity(int velocity) {
    this.velocity = velocity;
    return this;
  }

  public PerformanceBuilder withLength(long length) {
    this.length = Wholes.valueOf(length);
    return this;
  }

  public PerformanceBuilder withEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Specify which layers to add to the sequence.
   *
   * @param layers layers to add to the sequence.
   * @return project builder
   */
  public PerformanceBuilder withLayers(String... layers) {
    layerNamesToAdd = Lists.newArrayList(layers);
    Set<String> duplicates = layerNamesToAdd.stream()
        .filter(layer -> Collections.frequency(layerNamesToAdd, layer) > 1)
        .collect(Collectors.toSet());
    if (!duplicates.isEmpty()) {
      LOG.warn("Creating entity with layers {} that have duplicates {}", layers, duplicates);
    }
    LOG.debug("Layers to add : {}", layerNamesToAdd);
    return this;
  }

  /**
   * Specify trigger action to add to subsequent objects added to this project.
   *
   * @param trigger trigger for this sequence
   * @param action action to take place when this trigger fires
   * @return project builder
   */
  public PerformanceBuilder withTrigger(String trigger, Action action) {
    triggersToAdd.put(trigger, action);
    LOG.debug("Triggers to add : {}", triggersToAdd);
    return this;
  }

  public PerformanceBuilder withNoTriggers() {
    triggersToAdd.clear();
    return this;
  }

  /**
   * Offset to apply to the sequence.
   *
   * @param offset offset
   * @return project builder
   */
  public PerformanceBuilder withOffset(int offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Change program to given program number.
   *
   * @param program program number
   * @return builder
   */
  public PerformanceBuilder changeProgramTo(int program) {
    Channel channelConfiguration = createChannel();
    channelConfiguration.setProgram(program);
    addChannel(channelConfiguration);
    return this;
  }

  /**
   * Change program.
   *
   * @param programName program name to change to
   * @return builder
   */
  public PerformanceBuilder changeProgramTo(String programName) {
    Channel channelConfiguration = createChannel();
    channelConfiguration.setProgramName(programName);
    addChannel(channelConfiguration);
    return this;
  }

  private void addChannel(Channel channelConfiguration) {
    channelIds.add(channelConfiguration.getId());
    channelConfiguration.setNumber(channel);
    /*
     * For now we replace channel with same number, in future we might merge.
     */
    Optional<Channel> duplicate = projectContainer.getChannelStream()
        .filter(o -> o.getNumber() == channel)
        .findFirst();
    duplicate.ifPresent(projectContainer::removeChannel);
    projectContainer.addChannel(channelConfiguration);
  }

  /**
   * Add generic sequence.
   *
   * @return sequence builder
   */
  public PerformanceBuilder addSequence() {
    MutableSequenceConfiguration sequence = withDefaults(createSequence());
    addSequenceToContainer(applyParameters(sequence));
    return this;
  }

  /**
   * Add pattern.
   *
   * @param tick tick
   * @param pattern pattern
   * @return sequence builder
   */
  public PerformanceBuilder addPattern(Tick tick, int pattern) {
    return addPattern(tick, pattern, createNote(noteNumber, velocity,
        Notes.DEFAULT_DURATION));
  }

  /**
   * Add pattern.
   *
   * @param tick tick
   * @param pattern pattern
   * @return sequence builder
   */
  private PerformanceBuilder addPattern(Tick tick, int pattern, Note note) {
    this.tick = tick;
    MutableSequenceConfiguration sequence = withDefaultsForPattern(createPattern());
    new Setter(sequence)
        .set("bits", pattern)
        .set("note", note);

    addSequenceToContainer(applyParameters(sequence));
    return this;
  }

  public PerformanceBuilder addNotation(Tick tick, String notation) {
    return addNotation(tick, notation, DEFAULT_NOTATION_FORMAT);
  }

  private PerformanceBuilder addNotation(Tick tick, String notation, String format) {
    this.tick = tick;
    MutableSequenceConfiguration sequence = withDefaultsForNotation(createNotation());
    new Setter(sequence)
        .set("format", format)
        .set("notation", notation);

    addSequenceToContainer(applyParameters(sequence));
    return this;
  }

  public PerformanceBuilder removeSequence(SequenceConfiguration sequence) {
    projectContainer.removeSequence(sequence);
    return this;
  }

  private SequenceConfiguration applyParameters(MutableSequenceConfiguration sequence) {
    sequence.setName(name);
    sequence.setEnabled(enabled);
    sequence.setChannel(channel);
    sequence.setLength(length);
    sequence.setOffset(Wholes.valueOf(offset));
    if (sequence.getTick() == null) {
      sequence.setTick(tick);
    }
    if (sequence.getType() == null) {
      sequence.setType(typeName);
    }
    layerNamesToAdd.forEach(sequence::addLayer);
    triggersToAdd.forEach(sequence::addTrigger);

    /*
     * Apply properties
     */
    new Setter(sequence, Setter.Mode.DECLARED).applyProperties(properties);

    return sequence;
  }

  private Layer applyParameters(MutableLayer layer) {
    layerNamesToAdd.forEach(layer::addLayer);
    return layer;
  }

  private MutableTriggerConfiguration applyParameters(MutableTriggerConfiguration trigger) {
    trigger.setName(name);
    /*
     * Apply properties
     */
    new Setter(trigger, Setter.Mode.DECLARED).applyProperties(properties);
    return trigger;
  }
}
