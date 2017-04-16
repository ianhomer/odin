package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.StaticMeasureProvider;
import com.purplepip.odin.series.BeatsPerMinute;
import com.purplepip.odin.series.StaticBeatsPerMinute;

/**
 * Odin Sequencer Configuration.
 */
public class OdinSequencerConfiguration {
    private BeatsPerMinute beatsPerMinute = new StaticBeatsPerMinute(140);
    private MeasureProvider measureProvider = new StaticMeasureProvider(4);

    public OdinSequencerConfiguration setBeatsPerMinute(BeatsPerMinute beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
        return this;
    }

    public OdinSequencerConfiguration setMeasureProvider(MeasureProvider measureProvider) {
        this.measureProvider = measureProvider;
        return this;
    }

    public BeatsPerMinute getBeatsPerMinute() {
        return beatsPerMinute;
    }

    public MeasureProvider getMeasureProvider() {
        return measureProvider;
    }
}
