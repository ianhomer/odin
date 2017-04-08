package com.purplepip.odin.music;

import com.purplepip.odin.series.*;
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
    private long timeUnitsPerBar;

    public Metronome() {
        this(4);
    }

    public Metronome(long beatsPerBar) {
        this(beatsPerBar, -1);
    }

    public Metronome(long beatsPerBar, long length) {
        noteBarStart = new DefaultNote();
        noteMidBar = new DefaultNote(64);
        TimeUnitConverter converter = new TimeUnitConverter(TimeUnit.HALF_BEAT);
        this.length = converter.convert(TimeUnit.BEAT, length);
        this.timeUnitsPerBar = converter.convert(TimeUnit.BEAT, beatsPerBar);
        LOG.debug("Creating Metronome with {} beats per bar and length {} and time units per bar {}",
                beatsPerBar, length, timeUnitsPerBar);
        createNextEvent();
    }

    @Override
    public Event<Note> peek() {
        return nextEvent;
    }

    @Override
    public Event<Note> pop() {
        Event<Note> thisEvent = nextEvent;
        time = time + 2;
        if (length < 0 || time < length) {
            createNextEvent();
        } else {
            nextEvent = null;
        }
        return thisEvent;
    }

    @Override
    public TimeUnit getTimeUnits() {
        return TimeUnit.HALF_BEAT;
    }

    private void createNextEvent() {
        LOG.debug("Creating next event for time {}", time);
        if (time % timeUnitsPerBar == 0) {
            nextEvent = new DefaultEvent<>(noteBarStart, time);
        } else {
            nextEvent = new DefaultEvent<>(noteMidBar, time);
        }
    }
}
