package com.purplepip.odin.series;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series where time is in milliseconds relative to some origin, e.g. MIDI device start
 */
public class TickConvertedSeries implements Series<Note> {
    private static final Logger LOG = LoggerFactory.getLogger(TickConvertedSeries.class);
    private Series<Note> series;
    private DefaultTickConverter tickConverter;

    public TickConvertedSeries(Series<Note> series, DefaultTickConverter tickConverter) {
        this.series = series;
        this.tickConverter = tickConverter;
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
    public Tick getTick() {
        return tickConverter.getOutputTick();
    }

    private Event<Note> convertTimeUnits(Event<Note> event) {
        if (event == null) {
            LOG.debug("No event on series to convert");
            return null;
        }
        if (tickConverter.getOutputTick() == series.getTick()) {
            return event;
        }
        Note note = new DefaultNote(event.getValue().getNumber(), event.getValue().getVelocity(),
                tickConverter.convert(event.getValue().getDuration()));
        long time = tickConverter.convert(event.getTime());
        LOG.trace("Converted note {} to time {}", note, time);
        return new DefaultEvent<>(note, time);
    }
    
}
