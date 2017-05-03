package com.purplepip.odin.sequence;

import com.purplepip.odin.music.Note;

/**
 * Sequence Factory.
 */
public class SeriesTimeUnitConverterFactory {
  private DefaultTickConverter tickConverter;

  public SeriesTimeUnitConverterFactory(DefaultTickConverter tickConverter) {
    this.tickConverter = tickConverter;
  }

  public Sequence<Note> convertSeries(Sequence<Note> sequence) {
    return new TickConvertedSequence(sequence, tickConverter);
  }
}
