package com.purplepip.odin.sequence;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.Note;

/**
 * Abstract sequence.
 */
public abstract class MutableSequenceRuntime<S extends Sequence> implements SequenceRuntime<Note> {
  private MeasureProvider measureProvider;
  private S sequence;

  @Override
  public Tick getTick() {
    return sequence.getTick();
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  public void setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
  }

  public void setConfiguration(S sequence) {
    this.sequence = sequence;
    reload();
  }

  public S getConfiguration() {
    return sequence;
  }

  public void reload() {
  }

}
