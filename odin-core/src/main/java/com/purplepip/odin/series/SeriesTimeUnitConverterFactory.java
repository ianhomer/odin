package com.purplepip.odin.series;

import com.purplepip.odin.music.Note;

/**
 * Series Factory.
 */
public class SeriesTimeUnitConverterFactory {
    private DefaultTickConverter tickConverter;

    public SeriesTimeUnitConverterFactory(DefaultTickConverter tickConverter) {
        this.tickConverter = tickConverter;
    }

    public Series<Note> convertSeries(Series<Note> series) {
        return new TickConvertedSeries(series, tickConverter);
    }
}
