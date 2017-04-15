package com.purplepip.odin.series;

/**
 * Series clock that has the intelligence to know the timings of future beats.
 *
 * TODO : Support variation in BPM.
 */
public class Clock {
    private StaticBeatsPerMinute beatsPerMinute;
    private long microsecondsStart;

    public Clock(StaticBeatsPerMinute beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
    }

    public void start(long microsecondsStart) {
        this.microsecondsStart = microsecondsStart;
    }

    /**
     * Starting at next second can make debugging easier because microseconds position will start at a round
     * number.
     *
     * @param microsecondsStart
     */
    public void startAtNextSecond(long microsecondsStart) {
        this.microsecondsStart = 1000000 * (microsecondsStart / 1000000);
    }

    public BeatsPerMinute getBeatsPerMinute() {
        return beatsPerMinute;
    }

    public double getBeat(long microseconds) {
        return microseconds / beatsPerMinute.getMicroSecondsPerBeat();
    }

    public long getMicroSeconds(double beat) {
        return microsecondsStart + (long) (beatsPerMinute.getMicroSecondsPerBeat() * beat);
    }

}
