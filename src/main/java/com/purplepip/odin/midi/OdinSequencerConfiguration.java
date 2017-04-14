package com.purplepip.odin.midi;

/**
 * Odin Sequencer Configuration.
 */
public class OdinSequencerConfiguration {
    private boolean isCoreJavaSequencerEnabled = true;
    // TODO : Externalise defaults
    private int beatsPerMinute = 120;

    public OdinSequencerConfiguration setCoreJavaSequencerEnabled(boolean isCoreJavaSequencerEnabled) {
        this.isCoreJavaSequencerEnabled = isCoreJavaSequencerEnabled;
        return this;
    }

    public boolean isCoreJavaSequencerEnabled() {
        return isCoreJavaSequencerEnabled;
    }

    public OdinSequencerConfiguration setBeatsPerMinute(int beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
        return this;
    }

    public int getBeatsPerMinute() {
        return beatsPerMinute;
    }
}
