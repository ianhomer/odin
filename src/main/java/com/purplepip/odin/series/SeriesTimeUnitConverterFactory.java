package com.purplepip.odin.series;

import com.purplepip.odin.music.Note;

/**
 * Series Factory.
 */
public class SeriesTimeUnitConverterFactory {
    private TimeUnitConverter timeUnitConverter;

    public SeriesTimeUnitConverterFactory(TimeUnitConverter timeUnitConverter) {
        this.timeUnitConverter = timeUnitConverter;
    }

    public Series<Note> convertSeries(Series<Note> series) {
        return new TimeUnitConvertedSeries(series, timeUnitConverter);
    }
}
