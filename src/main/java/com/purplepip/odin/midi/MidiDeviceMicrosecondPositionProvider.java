package com.purplepip.odin.midi;

import com.purplepip.odin.series.MicrosecondPositionProvider;

import javax.sound.midi.MidiDevice;

/**
 * Microsecond position provider taking the microsecond position from a MIDI device.
 */
public class MidiDeviceMicrosecondPositionProvider implements MicrosecondPositionProvider {
    private MidiDevice midiDevice;

    public MidiDeviceMicrosecondPositionProvider(MidiDevice midiDevice) {
        this.midiDevice = midiDevice;
    }

    @Override
    public long getMicrosecondPosition() {
        return midiDevice.getMicrosecondPosition();
    }
}
