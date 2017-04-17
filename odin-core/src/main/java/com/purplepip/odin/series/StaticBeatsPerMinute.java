package com.purplepip.odin.series;

/**
 * Static beats per minute.
 */
public class StaticBeatsPerMinute implements BeatsPerMinute {
    private static final int MICROSECONDS_PER_MINUTE = 60000000;

    private int beatsPerMinute;
    private long microsecondsPerBeat;

    public StaticBeatsPerMinute(int beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
        microsecondsPerBeat = MICROSECONDS_PER_MINUTE / beatsPerMinute;
    }

    @Override
    public int getBeatsPerMinute() {
        return beatsPerMinute;
    }

    @Override
    public long getMicroSecondsPerBeat() {
        return microsecondsPerBeat ;
    }
}
