package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.music.sequence.DefaultMetronome;
import com.purplepip.odin.music.sequence.DefaultPattern;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Ticks;

/**
 * Convenience class for building up sequences.
 */
public class ProjectBuilder {
  private static final int DEFAULT_NOTE = 60;
  private static final int DEFAULT_VELOCITY = 40;
  private static final int DEFAULT_DURATION = 1;

  private Project project;
  private int channel = 0;
  private int note = DEFAULT_NOTE;
  private int velocity = DEFAULT_VELOCITY;
  private int length = -1;


  public ProjectBuilder(Project project) {
    this.project = project;
  }

  private Metronome withDefaults(Metronome metronome) {
    metronome.setTick(createTick(Ticks.HALF));
    metronome.setFlowName(MetronomeFlow.class.getName());
    metronome.setNoteBarStart(createNote(DEFAULT_NOTE, DEFAULT_VELOCITY, DEFAULT_DURATION));
    metronome.setNoteMidBar(createNote(64, metronome.getNoteBarStart().getVelocity() / 2,
        DEFAULT_DURATION));
    return metronome;
  }

  private Pattern withDefaults(Pattern pattern) {
    pattern.setFlowName(PatternFlow.class.getName());
    return pattern;
  }

  private Tick withDefaults(Tick tick) {
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
    project.addSequence(applyParameters(metronome));
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

    project.addSequence(applyParameters(sequence));
    return this;
  }

  private Sequence applyParameters(MutableSequence sequence) {
    sequence.setOffset(0);
    sequence.setChannel(channel);
    sequence.setLength(length);
    return sequence;
  }
}
