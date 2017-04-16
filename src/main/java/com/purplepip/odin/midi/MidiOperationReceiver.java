package com.purplepip.odin.midi;

import com.purplepip.odin.OdinException;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;

import javax.sound.midi.*;

/**
 * MIDI operation receiver.
 */
public class MidiOperationReceiver implements OperationReceiver {
    private MidiDevice midiDevice;

    public MidiOperationReceiver(MidiDevice midiDevice) {
        this.midiDevice = midiDevice;
    }

    @Override
    public void send(Operation operation, long time) throws OdinException {
        MidiMessage midiMessage;
        try {
            midiMessage = createMidiMessage(operation);
        } catch (InvalidMidiDataException e) {
            throw new OdinException("Cannot create MIDI message for " + operation, e);
        }
        try {
            midiDevice.getReceiver().send(midiMessage, time);
        } catch (MidiUnavailableException e) {
            throw new OdinException("Cannot send MIDI message for " + midiMessage, e);
        }
    }

    private MidiMessage createMidiMessage(Operation operation) throws InvalidMidiDataException, OdinException {
        switch (operation.getType()) {
            case ON:
                return new ShortMessage(ShortMessage.NOTE_ON, operation.getChannel(), operation.getNumber(), operation.getVelocity());
            case OFF:
                return new ShortMessage(ShortMessage.NOTE_OFF, operation.getChannel(), operation.getNumber(), operation.getVelocity());
        }
        throw new OdinException("MIDI message not set for " + operation);
    }
}
