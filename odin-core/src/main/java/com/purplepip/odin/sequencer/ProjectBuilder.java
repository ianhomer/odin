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

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.music.sequence.DefaultMetronome;
import com.purplepip.odin.music.sequence.DefaultPattern;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Ticks;
import java.util.Optional;

/**
 * Convenience class for building up sequences.
 */
public class ProjectBuilder {
  private static final int DEFAULT_NOTE = 60;
  private static final int DEFAULT_VELOCITY = 40;
  private static final int DEFAULT_DURATION = 1;

  private ProjectContainer projectContainer;
  private int channel;
  private int note = DEFAULT_NOTE;
  private int velocity = DEFAULT_VELOCITY;
  private int length = -1;
  private int offset;


  public ProjectBuilder(ProjectContainer projectContainer) {
    this.projectContainer = projectContainer;
  }

  private Metronome withDefaults(Metronome metronome) {
    metronome.setTick(createTick(Ticks.HALF));
    metronome.setFlowName(MetronomeFlow.class.getName());
    metronome.setNoteBarStart(createNote(DEFAULT_NOTE, DEFAULT_VELOCITY, DEFAULT_DURATION));
    metronome.setNoteMidBar(createNote(64, metronome.getNoteBarStart().getVelocity() / 2,
        DEFAULT_DURATION));
    return metronome;
  }

  private static Pattern withDefaults(Pattern pattern) {
    pattern.setFlowName(PatternFlow.class.getName());
    return pattern;
  }

  private static Tick withDefaults(Tick tick) {
    return tick;
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
   * Create Note.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @param note note number
   * @param velocity velocity
   * @param duration duration
   * @return note
   */
  protected Note createNote(int note, int velocity, long duration) {
    return new DefaultNote(note, velocity, duration);
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

  /**
   * Add metronome.
   *
   * @return this sequence builder
   */
  public ProjectBuilder addMetronome() {
    Metronome metronome = withDefaults(createMetronome());
    projectContainer.addSequence(applyParameters(metronome));
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

    projectContainer.addSequence(applyParameters(sequence));
    return this;
  }

  private Sequence applyParameters(MutableSequence sequence) {
    sequence.setOffset(0);
    sequence.setChannel(channel);
    sequence.setLength(length);
    sequence.setOffset(offset);
    return sequence;
  }
}
