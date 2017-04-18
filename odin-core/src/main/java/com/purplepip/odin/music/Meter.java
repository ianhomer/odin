package com.purplepip.odin.music;

import com.purplepip.odin.series.Clock;

/**
 * Defines how beats combine into measures.
 */
public class Meter {
    private Clock clock;
    private MeasureProvider measureProvider;

    public Meter(Clock clock, MeasureProvider measureProvider) {
        this.clock = clock;
        this.measureProvider = measureProvider;
    }

    public Clock getClock() {
        return clock;
    }

    public MeasureProvider getMeasureProvider() {
        return measureProvider;
    }

}
