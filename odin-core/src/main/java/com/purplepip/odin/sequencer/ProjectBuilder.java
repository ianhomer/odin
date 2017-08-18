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

package com.purplepip.odin.sequencer;

import com.google.common.collect.Lists;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.NotationFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.DefaultMetronome;
import com.purplepip.odin.music.sequence.DefaultNotation;
import com.purplepip.odin.music.sequence.DefaultPattern;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.layer.DefaultLayer;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.layer.MutableLayer;
import com.purplepip.odin.sequence.tick.DefaultTick;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Convenience class for building up sequences.
 */
@Slf4j
public class ProjectBuilder {
  private static final int DEFAULT_NOTE = 60;
  public static final int DEFAULT_VELOCITY = 40;
  public static final Rational DEFAULT_DURATION = Wholes.ONE;
  private static final String DEFAULT_NOTATION_FORMAT = "natural";

  private ProjectContainer projectContainer;
  private String name;
  private int channel;
  private int note;
  private int velocity;
  private int length;
  private int offset;
  private List<String> layerNamesToAdd = new ArrayList<>();
  private List<Long> sequenceIds = new ArrayList<>();
  private List<Long> channelIds = new ArrayList<>();
  private List<Long> layerIds = new ArrayList<>();

  public ProjectBuilder(ProjectContainer projectContainer) {
    this.projectContainer = projectContainer;
    reset();
  }

  /**
   * Clear the project builder so that it can be used again.  Note that the underlying project is
   * NOT cleared.
   */
  public void reset() {
    name = null;
    channel = 0;
    note = DEFAULT_NOTE;
    velocity = DEFAULT_VELOCITY;
    length = -1;
    offset = 0;
    layerNamesToAdd.clear();
    sequenceIds.clear();
    channelIds.clear();
    layerIds.clear();
  }

  public Layer getLayer(String name) {
    return projectContainer.getLayerStream().filter(l -> name.equals(l.getName())).findFirst()
        .orElse(null);
  }

  /*
   * Get sequence IDs added so that we can inspect the sequences that were created.  Note that
   * this is an ordered list in the order that they were added.  Note that sequences in a project
   * do not have a fixed ordering so we can not rely on the iteration of the project sequences.
   */
  public Sequence getSequenceByOrder(int id) {
    return projectContainer.getSequence(sequenceIds.get(id));
  }

  /*
   * As per getSequenceByOrder logic.
   */
  public Channel getChannelByOrder(int id) {
    return projectContainer.getChannel(channelIds.get(id));
  }

  /*
   * As per getSequenceByOrder logic.
   */
  public Layer getLayerByOrder(int id) {
    return projectContainer.getLayer(layerIds.get(id));
  }


  private Metronome withDefaults(Metronome metronome) {
    metronome.setTick(createTick(Ticks.HALF));
    metronome.setFlowName(MetronomeFlow.NAME);
    metronome.setNoteBarStart(createNote(DEFAULT_NOTE, DEFAULT_VELOCITY, DEFAULT_DURATION));
    metronome.setNoteBarMid(createNote(64, metronome.getNoteBarStart().getVelocity() / 2,
        DEFAULT_DURATION));
    return metronome;
  }

  private static Pattern withDefaults(Pattern pattern) {
    pattern.setFlowName(PatternFlow.NAME);
    return pattern;
  }

  private static Notation withDefaults(Notation notation) {
    notation.setFlowName(NotationFlow.NAME);
    return notation;
  }

  private static Tick withDefaults(Tick tick) {
    return tick;
  }

  private MutableLayer withDefaults(MutableLayer layer) {
    layer.setTick(createTick(Ticks.BEAT));
    layer.setLength(length);
    layer.setOffset(offset);
    return layer;
  }

  /**
   * Create Layer.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return metronome
   */
  protected MutableLayer createLayer() {
    return new DefaultLayer();
  }

  /**
   * Create Metronome.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return metronome
   */
  protected Metronome createMetronome() {
    return new DefaultMetronome();
  }

  /**
   * Create Notation.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return notation
   */
  protected Notation createNotation() {
    return new DefaultNotation();
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
  protected Note createNote(int number, int velocity, Rational duration) {
    return new DefaultNote(number, velocity, duration);
  }

  /**
   * Create Pattern.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return pattern
   */
  protected Pattern createPattern() {
    return new DefaultPattern();
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

  private void addSequence(Sequence sequence) {
    sequenceIds.add(sequence.getId());
    projectContainer.addSequence(sequence);
  }

  /**
   * Add metronome.
   *
   * @return this sequence builder
   */
  public ProjectBuilder addMetronome() {
    Metronome metronome = withDefaults(createMetronome());
    addSequence(applyParameters(metronome));
    return this;
  }

  /**
   * Add layers with the given names.
   *
   * @param names names of layers
   * @return this project builder
   */
  public ProjectBuilder addLayer(String... names) {
    for (String name : names) {
      MutableLayer layer = withDefaults(createLayer());
      layer.setName(name);
      applyParameters(layer);
      layerIds.add(layer.getId());
      projectContainer.addLayer(layer);
    }
    return this;
  }

  public ProjectBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ProjectBuilder withChannel(int channel) {
    this.channel = channel;
    return this;
  }


  public ProjectBuilder withNote(int note) {
    this.note = note;
    return this;
  }

  public ProjectBuilder withVelocity(int velocity) {
    this.velocity = velocity;
    return this;
  }

  public ProjectBuilder withLength(int length) {
    this.length = length;
    return this;
  }

  /**
   * Specify which layers to add to the sequence.
   *
   * @param layers layers to add to the sequence.
   * @return project builder
   */
  public ProjectBuilder withLayers(String... layers) {
    layerNamesToAdd = Lists.newArrayList(layers);
    Set<String> duplicates = layerNamesToAdd.stream()
        .filter(layer -> Collections.frequency(layerNamesToAdd, layer) > 1)
        .collect(Collectors.toSet());
    if (duplicates.size() > 0) {
      LOG.warn("Creating entity with layers {} that have duplicates {}", layers, duplicates);
    }
    LOG.debug("Layers to add : {}", layerNamesToAdd);
    return this;
  }

  /**
   * Offset to apply to the sequence.
   *
   * @param offset offset
   * @return project builder
   */
  public ProjectBuilder withOffset(int offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Change program to given program number.
   *
   * @param program program number
   * @return builder
   */
  public ProjectBuilder changeProgramTo(int program) {
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
  public ProjectBuilder changeProgramTo(String programName) {
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
   * Add pattern.
   *
   * @param tick tick
   * @param pattern pattern
   * @return sequence builder
   */
  public ProjectBuilder addPattern(Tick tick, int pattern) {
    return addPattern(tick, pattern, createNote(note, velocity,
        DEFAULT_DURATION));
  }

  /**
   * Add pattern.
   *
   * @param tick tick
   * @param pattern pattern
   * @return sequence builder
   */
  private ProjectBuilder addPattern(Tick tick, int pattern, Note note) {
    Pattern sequence = withDefaults(createPattern());
    sequence.setBits(pattern);
    sequence.setTick(withDefaults(createTick(tick)));
    sequence.setNote(note);

    addSequence(applyParameters(sequence));
    return this;
  }

  public ProjectBuilder addNotation(Tick tick, String notation) {
    return addNotation(tick, notation, DEFAULT_NOTATION_FORMAT);
  }

  private ProjectBuilder addNotation(Tick tick, String notation, String format) {
    Notation sequence = withDefaults(createNotation());
    sequence.setTick(withDefaults(createTick(tick)));
    sequence.setFormat(format);
    sequence.setNotation(notation);

    addSequence(applyParameters(sequence));
    return this;
  }

  public ProjectBuilder removeSequence(Sequence sequence) {
    projectContainer.removeSequence(sequence);
    return this;
  }

  private Sequence applyParameters(MutableSequence sequence) {
    sequence.setName(name);
    sequence.setOffset(0);
    sequence.setChannel(channel);
    sequence.setLength(length);
    sequence.setOffset(offset);
    layerNamesToAdd.forEach(sequence::addLayer);
    return sequence;
  }

  private Layer applyParameters(MutableLayer layer) {
    layerNamesToAdd.forEach(layer::addLayer);
    return layer;
  }
}
