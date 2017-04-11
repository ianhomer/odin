package com.purplepip.odin.midi;

import java.util.Comparator;

/**
 * Comparator for a MIDI message.
 */
public class MidiMessageComparator implements Comparator<MidiMessageEvent> {
    public int compare(MidiMessageEvent x, MidiMessageEvent y) {
        if (x.getTime() < y.getTime()) {
            return -1;
        } else if (x.getTime() > y.getTime()) {
            return 1;
        }
        return 0;
    }
}
