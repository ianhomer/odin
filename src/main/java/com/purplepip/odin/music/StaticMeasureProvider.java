package com.purplepip.odin.music;

import com.purplepip.odin.series.Tock;

/**
 * A simple static measure provider, e.g. static time signature
 */
public class StaticMeasureProvider implements MeasureProvider {
    private int beatsPerBar;

    public StaticMeasureProvider(int beatsPerBar) {
        this.beatsPerBar = beatsPerBar;
    }

    @Override
    public long getMeasureCount(Tock tock) {
        return tock.getCount() * tock.getTick().getDenominator() / ( beatsPerBar * tock.getTick().getNumerator());
    }

    @Override
    public int getMeasureBeats(Tock tock) {
        return beatsPerBar;
    }

    @Override
    public long getTickPosition(Tock tock) {
        return tock.getCount() % (beatsPerBar * tock.getTick().getDenominator() / tock.getTick().getNumerator());
    }
}
