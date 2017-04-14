package com.purplepip.odin.series;

/**
 * Series clock that has the intelligence to know the timings of future beats.
 *
 * TODO : Support variation in BPM.
 */
public class Clock {
    private static final float SECONDS_PER_MINUTE = 60.0f;

    private int beatsPerMinute;
    private double secondsPerBeat;

    public Clock(int beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
        this.secondsPerBeat = SECONDS_PER_MINUTE / beatsPerMinute;
    }

    public int getBeatsPerMinute() {
        return beatsPerMinute;
    }

    public double getBeat(double seconds) {
        return seconds / secondsPerBeat;
    }

    public double getSeconds(double beat) {
        return secondsPerBeat * beat;
    }





}
