package com.purplepip.odin.sequence;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.Note;

/**
 * Abstract sequence.
 */
public abstract class MutableSequence<A extends SequenceConfiguration> implements Sequence<Note> {
  private MeasureProvider measureProvider;
  private A configuration;

  @Override
  public Tick getTick() {
    return configuration.getTick();
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  public void setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
  }

  public void setConfiguration(A configuration) {
    this.configuration = configuration;
    reload();
  }

  public A getConfiguration() {
    return configuration;
  }

  public void reload() {
  }

}
