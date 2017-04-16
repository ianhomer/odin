package com.purplepip.odin.midi;

import com.purplepip.odin.sequencer.Operation;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * A MIDI message bound to a given time.
 */
public class MidiMessageEvent {
    private MidiMessage midiMessage;
    private long time;

    public MidiMessageEvent(Operation operation, long time) throws InvalidMidiDataException {
        switch (operation.getType()) {
            case ON:
                this.midiMessage =
                        new ShortMessage(ShortMessage.NOTE_ON, operation.getChannel(), operation.getNumber(), operation.getVelocity());
                break;
            case OFF:
                this.midiMessage =
                        new ShortMessage(ShortMessage.NOTE_OFF, operation.getChannel(), operation.getNumber(), operation.getVelocity());
                break;
        }
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public MidiMessage getMidiMessage() {
        return midiMessage;
    }

}
