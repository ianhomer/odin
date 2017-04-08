package com.purplepip.odin.midi;

/**
 * Odin Sequencer Configuration.
 */
public class OdinSequencerConfiguration {
    private boolean isCoreJavaSequencerEnabled = true;
    // TODO : Externalise defaults
    private long beatsPerMinute = 120;

    public OdinSequencerConfiguration setCoreJavaSequencerEnabled(boolean isCoreJavaSequencerEnabled) {
        this.isCoreJavaSequencerEnabled = isCoreJavaSequencerEnabled;
        return this;
    }

    public boolean isCoreJavaSequencerEnabled() {
        return isCoreJavaSequencerEnabled;
    }

    public OdinSequencerConfiguration setBeatsPerMinute(long beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
        return this;
    }

    public long getBeatsPerMinute() {
        return beatsPerMinute;
    }
}
