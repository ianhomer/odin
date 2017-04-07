package com.purplepip.odin.music;

import com.purplepip.odin.series.DefaultEvent;
import com.purplepip.odin.series.Event;
import com.purplepip.odin.series.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metronome Series.
 */
public class Metronome implements Series<Note> {
    private static final Logger LOG = LoggerFactory.getLogger(Metronome.class);

    private Event<Note> nextEvent;
    private Note note;
    private long time = 0;
    private long length;

    public Metronome() {
        this(-1);
    }

    public Metronome(long length) {
        LOG.debug("Creating Metronome with length {}", length);
        note = new DefaultNote();
        this.length = length;
        createNextEvent();
    }

    @Override
    public Event<Note> peek() {
        return nextEvent;
    }

    @Override
    public Event<Note> pop() {
        Event<Note> thisEvent = nextEvent;
        time++;
        if (length < 0 || time < length) {
            createNextEvent();
        } else {
            nextEvent = null;
        }
        return thisEvent;
    }

    private void createNextEvent() {
        LOG.debug("Creating next event for time {}", time);
        nextEvent = new DefaultEvent<>(note, time);
    }
}
