package com.purplepip.odin.sequence;

import com.purplepip.odin.music.Note;

/**
 * SequenceRuntime Factory.
 */
public class SeriesTimeUnitConverterFactory {
  private DefaultTickConverter tickConverter;

  public SeriesTimeUnitConverterFactory(DefaultTickConverter tickConverter) {
    this.tickConverter = tickConverter;
  }

  public SequenceRuntime<Note> convertSeries(SequenceRuntime<Note> sequenceRuntime) {
    return new TickConvertedSequenceRuntime(sequenceRuntime, tickConverter);
  }
}
