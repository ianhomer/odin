package com.purplepip.odin.music;

import com.purplepip.odin.series.DefaultEvent;
import com.purplepip.odin.series.Event;
import com.purplepip.odin.series.Series;
import com.purplepip.odin.series.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metronome Series.
 */
public class Metronome implements Series<Note> {
    private static final Logger LOG = LoggerFactory.getLogger(Metronome.class);

    private Event<Note> nextEvent;
    private Note noteBarStart;
    private Note noteMidBar;
    private long time = 0;
    private long length;
    private long beatsPerBar;

    public Metronome() {
        this(4);
    }

    public Metronome(long beatsPerBar) {
        this(beatsPerBar, -1);
    }

    public Metronome(long beatsPerBar, long length) {
        LOG.debug("Creating Metronome with {} beats per bar and length {}", length);
        noteBarStart = new DefaultNote();
        noteMidBar = new DefaultNote(64);
        this.length = length;
        this.beatsPerBar = beatsPerBar;
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

    @Override
    public TimeUnit getTimeUnits() {
        return TimeUnit.BEAT;
    }

    private void createNextEvent() {
        LOG.debug("Creating next event for time {}", time);
        if (time % beatsPerBar == 0) {
            nextEvent = new DefaultEvent<>(noteBarStart, time);
        } else {
            nextEvent = new DefaultEvent<>(noteMidBar, time);
        }
    }
}
