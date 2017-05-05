package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.logic.MetronomeLogic;
import com.purplepip.odin.music.logic.PatternLogic;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.DefaultSequenceRuntime;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.logic.Logic;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Convenience class for building up sequences.
 */
public class SequenceBuilder {
  private MeasureProvider measureProvider;
  private OdinSequencer sequencer;
  private int channel = 0;
  private int note = 60;
  private int velocity = 60;

  public SequenceBuilder(OdinSequencer sequencer, MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
    this.sequencer = sequencer;
  }

  private <S extends Sequence, L extends Logic<S, Note>>
      MutableSequenceRuntime<S, Note>
      createMutableSequenceRuntime(Class<S> clazz, L logic) {
    MutableSequenceRuntime<S, Note> sequenceRuntime = new DefaultSequenceRuntime<>(logic);
    sequenceRuntime.setConfiguration(logic.getSequence());
    sequenceRuntime.setMeasureProvider(measureProvider);
    return sequenceRuntime;
  }

  /**
   * Add metronome.
   *
   * @return this sequence builder
   * @throws OdinException exception
   */
  public SequenceBuilder addMetronome() throws OdinException {
    MutableSequenceRuntime<Metronome, Note> sequenceRuntime = createMutableSequenceRuntime(
        Metronome.class, new MetronomeLogic(new Metronome()));
    sequencer.addSequence(sequenceRuntime, 0, 9);
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
    Pattern configuration = new Pattern();
    configuration.setPattern(pattern);
    configuration.setTick(tick);
    configuration.setNote(defaultNote);
    MutableSequenceRuntime<Pattern, Note> sequenceRuntime = createMutableSequenceRuntime(
        Pattern.class, new PatternLogic(configuration));
    sequencer.addSequence(sequenceRuntime,0, channel);
    return this;
  }
}
