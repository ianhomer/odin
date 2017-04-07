package com.purplepip.odin.midi;

/**
 * Odin Sequencer Configuration.
 */
public class OdinSequencerConfiguration {
    private boolean isCoreJavaSequencerEnabled = true;

    public OdinSequencerConfiguration setCoreJavaSequencerEnabled(boolean isCoreJavaSequencerEnabled) {
        this.isCoreJavaSequencerEnabled = isCoreJavaSequencerEnabled;
        return this;
    }

    public boolean isCoreJavaSequencerEnabled() {
        return isCoreJavaSequencerEnabled;
    }
}
