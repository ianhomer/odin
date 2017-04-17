package com.purplepip.odin.midix;

import javax.sound.midi.MidiMessage;

/**
 * Raw MIDI Messages that take raw bytes.  We use this technique as it pushes more logic to a generic layer than
 * can be used directly on Android.
 */
public class RawMidiMessage extends MidiMessage {
    public RawMidiMessage(byte[] buffer) {
        super(buffer);
    }

    @Override
    public Object clone() {
        byte[] newData = new byte[length];
        System.arraycopy(data, 0, newData, 0, newData.length);
        RawMidiMessage msg = new RawMidiMessage(newData);
        return msg;
    }
}
