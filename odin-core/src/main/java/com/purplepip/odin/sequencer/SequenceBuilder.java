package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.logic.MetronomeLogic;
import com.purplepip.odin.music.logic.PatternLogic;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.AbstractSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;

/**
 * Convenience class for building up sequences.
 */
public class SequenceBuilder {
  private OdinSequencer sequencer;
  private int channel = 0;
  private int note = 60;
  private int velocity = 60;

  public SequenceBuilder(OdinSequencer sequencer) {
    this.sequencer = sequencer;
  }

  /**
   * Add metronome.
   *
   * @return this sequence builder
   * @throws OdinException exception
   */
  public SequenceBuilder addMetronome() throws OdinException {
    sequencer.addSequence(applyParameters(new Metronome()));
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
    return addPattern(tick, pattern, new DefaultNote(note, velocity));
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
    Pattern sequence = new Pattern();
    sequence.setPattern(pattern);
    sequence.setTick(tick);
    sequence.setNote(defaultNote);

    sequencer.addSequence(applyParameters(sequence));
    return this;
  }

  private Sequence applyParameters(AbstractSequence sequence) {
    sequence.setOffset(0);
    sequence.setChannel(channel);
    return sequence;
  }
}
