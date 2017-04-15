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
    private MeasureProvider measureProvider;

    public Metronome(MeasureProvider measureProvider) {
        this(measureProvider, -1);
    }

    public Metronome(MeasureProvider measureProvider, long length) {
        noteBarStart = new DefaultNote();
        noteMidBar = new DefaultNote(64, noteBarStart.getVelocity() / 2);
        // TODO : We don't need clock for this converter, but we should make this more robust than setting null
        TickConverter converter = new SameTimeUnitTickConverter(Tick.BEAT, Tick.HALF);
        this.length = converter.convert(length);
        this.measureProvider = measureProvider;
        LOG.debug("Creating Metronome with length {} and measure provider {}",
                length, measureProvider);
        createNextEvent();
    }

    @Override
    public Tick getTick() {
        return Tick.HALF;
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


    private void createNextEvent() {
        LOG.debug("Creating next event for time {}", time);
        Note note;
        if (measureProvider.getTickPosition(new Tock(getTick(), time)) == 0) {
            note = noteBarStart;
        } else {
            note = noteMidBar;
        }
        nextEvent = new DefaultEvent<>(note, time);
    }
}
