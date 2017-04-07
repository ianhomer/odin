package com.purplepip.odin.midi;

/**
 * Custom runtime MIDI exception;
 */
public class MidiRuntimeException extends RuntimeException {
    public MidiRuntimeException(String message) {
        super(message);
    }
}
