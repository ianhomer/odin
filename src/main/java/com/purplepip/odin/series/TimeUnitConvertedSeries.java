package com.purplepip.odin.series;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series where time is in milliseconds relative to some origin, e.g. MIDI device start
 */
public class TimeUnitConvertedSeries implements Series<Note> {
    private static final Logger LOG = LoggerFactory.getLogger(TimeUnitConvertedSeries.class);
    private Series<Note> series;
    private TimeUnitConverter timeUnitConverter;

    public TimeUnitConvertedSeries(Series<Note> series, TimeUnitConverter timeUnitConverter) {
        this.series = series;
        this.timeUnitConverter = timeUnitConverter;
    }

    @Override
    public Event<Note> peek() {
        return convertTimeUnits(series.peek());
    }

    @Override
    public Event<Note> pop() {
        return convertTimeUnits(series.pop());
    }

    @Override
    public TimeUnit getTimeUnits() {
        return timeUnitConverter.getOutputTimeUnit();
    }

    private Event<Note> convertTimeUnits(Event<Note> event) {
        if (event == null) {
            LOG.debug("No event on series to convert");
            return null;
        }
        if (timeUnitConverter.getOutputTimeUnit() == series.getTimeUnits()) {
            return event;
        }
        Note note = new DefaultNote(event.getValue().getNumber(), event.getValue().getVelocity(),
                timeUnitConverter.convert(series.getTimeUnits(), event.getValue().getDuration()));
        long time = timeUnitConverter.convert(series.getTimeUnits(), event.getTime());
        LOG.trace("Converted note {} to time {}", note, time);
        return new DefaultEvent<>(note, time);
    }
}
