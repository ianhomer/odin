package com.purplepip.odin;

/**
 * Custom runtime MIDI exception;
 */
public class OdinRuntimeException extends RuntimeException {
    public OdinRuntimeException(String message) {
        super(message);
    }
}
