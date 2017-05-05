package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.Metronome;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.Pattern;
import com.purplepip.odin.sequence.DefaultSequenceRuntime;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.Sequence;
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

  private <T extends Sequence> MutableSequenceRuntime<T>
      createMutableSequenceRuntime(Class<T> clazz, T sequence) {
    MutableSequenceRuntime<T> sequenceRuntime = new DefaultSequenceRuntime<>(
        sequence.createEventProvider(measureProvider));
    sequenceRuntime.setConfiguration(sequence);
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
    MutableSequenceRuntime<Metronome> sequenceRuntime = createMutableSequenceRuntime(
        Metronome.class, new Metronome());
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
    MutableSequenceRuntime<Pattern> sequenceRuntime = createMutableSequenceRuntime(
        Pattern.class, configuration);
    sequencer.addSequence(sequenceRuntime,0, channel);
    return this;
  }
}
