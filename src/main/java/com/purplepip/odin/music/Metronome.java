package com.purplepip.odin.series;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;

/**
 * Metronome Series.
 */
public class Metronome implements Series {
    private Note note;
    private int beatsPerMinute = 120;

    public Metronome() {
        note = new DefaultNote();
    }


}
