package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.AbstractSequence;
import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;

/**
 * Convenience class for building up sequences.
 */
public class SequenceBuilder {
  private Project project;
  private int channel = 0;
  private int note = 60;
  private int velocity = 60;

  public SequenceBuilder(Project project) {
    this.project = project;
  }

  /**
   * Create Metronome.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return metronome
   */
  protected Metronome createMetronome() {
    return new Metronome();
  }

  /**
   * Create Note.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return note
   */
  protected Note createNote(int note, int velocity, int duration) {
    return new DefaultNote(note, velocity, duration);
  }

  /**
   * Create Pattern.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return pattern
   */
  protected Pattern createPattern() {
    return new Pattern();
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
   * @throws OdinException exception
   */
  public SequenceBuilder addMetronome() throws OdinException {
    Metronome metronome = createMetronome();
    project.addSequence(applyParameters(metronome));
    return this;
  }

  public SequenceBuilder withChannel(int channel) {
    this.channel = channel;
    return this;
  }

  public SequenceBuilder withNote(int note) {
    this.note = note;
    return this;
  }

  public SequenceBuilder withVelocity(int velocity) {
    this.velocity = velocity;
    return this;
  }


  /**
   * Add pattern.
   *
   * @param tick tick
   * @param pattern pattern
   * @return sequence builder
   * @throws OdinException exception
   */
  public SequenceBuilder addPattern(Tick tick, int pattern) throws OdinException {
    return addPattern(tick, pattern, createNote(note, velocity, 1));
  }

  /**
   * Add pattern.
   *
   * @param tick tick
   * @param pattern pattern
   * @param defaultNote default note
   * @return sequence builder
   * @throws OdinException exception
   */
  public SequenceBuilder addPattern(Tick tick, int pattern, Note defaultNote)
      throws OdinException {
    Pattern sequence = createPattern();
    sequence.setBits(pattern);
    sequence.setTick(createTick(tick));
    sequence.setNote(defaultNote);

    project.addSequence(applyParameters(sequence));
    return this;
  }

  private Sequence<Note> applyParameters(AbstractSequence<Note> sequence) {
    sequence.setOffset(0);
    sequence.setChannel(channel);
    return sequence;
  }
}
