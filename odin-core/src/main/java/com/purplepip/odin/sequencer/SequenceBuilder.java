package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.Metronome;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.Pattern;
import com.purplepip.odin.sequence.Tick;

/**
 * Convenience class for building up sequences.
 */
public class SequenceBuilder {
  MeasureProvider measureProvider;
  OdinSequencer sequencer;
  int channel = 0;
  int note = 60;
  int velocity = 60;

  public SequenceBuilder(OdinSequencer sequencer, MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
    this.sequencer = sequencer;
  }

  public SequenceBuilder addMetronome() throws OdinException {
    sequencer.addSeries(new Metronome(measureProvider), 0, 9);
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
    addPattern(tick, pattern, new DefaultNote(note, velocity));
    return this;
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
    sequencer.addSeries(new Pattern(measureProvider, tick, pattern, defaultNote),
        0, channel);
    return this;
  }
}
