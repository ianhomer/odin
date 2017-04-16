package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.Series;

/**
 * Series driving a MIDI track.
 */
public class SeriesTrack {
    private Series<Note> series;
    private int channel;

    public SeriesTrack(Series<Note> series, int channel) {
        this.series = series;
        this.channel = channel;
    }

    public int getChannel() {
        return channel;
    }

    public Series<Note> getSeries() {
        return series;
    }
}
