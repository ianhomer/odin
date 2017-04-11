package com.purplepip.odin.midi;

import javax.sound.midi.MidiMessage;

/**
 * A MIDI message bound to a given time.
 */
public class MidiMessageEvent {
    private MidiMessage midiMessage;
    private long time;

    public MidiMessageEvent(MidiMessage midiMessage, long time) {
        this.midiMessage = midiMessage;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public MidiMessage getMidiMessage() {
        return midiMessage;
    }

}
