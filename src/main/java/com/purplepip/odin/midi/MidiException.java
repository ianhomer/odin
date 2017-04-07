package com.purplepip.odin.midi;

/**
 * Odin MIDI exceptions
 */
public class MidiException extends Exception {
    public MidiException(String message) {
        super(message);
    }

    public MidiException(String message, Exception e) {
        super(message, e);
    }

    public MidiException(Exception e) {
        super(e);
    }
}
