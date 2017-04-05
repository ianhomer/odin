package com.purplepip.odin.music;

import com.purplepip.odin.series.DefaultEvent;
import com.purplepip.odin.series.Event;
import com.purplepip.odin.series.Series;

/**
 * Metronome Series.
 */
public class Metronome implements Series {
    private Note note;
    private int beatsPerMinute = 120;

    public Metronome() {
        note = new DefaultNote();
    }

    @Override
    public Event peek() {
        return new DefaultEvent<>(note, 0);
    }

    @Override
    public Event pop() {
        return new DefaultEvent<>(note, 0);
    }
}
