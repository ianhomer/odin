package com.purplepip.odin.midi;

import com.purplepip.odin.midi.experiments.MidiSequenceExperiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * Comparator for a MIDI message.
 */
public class MidiMessageComparator implements Comparator<MidiMessageEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(MidiMessageComparator.class);

    public int compare(MidiMessageEvent x, MidiMessageEvent y) {
        if (x == null) {
            LOG.warn("Midi message event should not be null");
            if (y == null) {
                return 0;
            }
            return -1;
        } else if (y == null) {
            LOG.warn("Midi message event should not be null");
            return 1;
        }
        if (x.getTime() < y.getTime()) {
            return -1;
        } else if (x.getTime() > y.getTime()) {
            return 1;
        }
        return 0;
    }
}
