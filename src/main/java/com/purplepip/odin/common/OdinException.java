package com.purplepip.odin;

/**
 * Odin MIDI exceptions
 */
public class OdinException extends Exception {
    public OdinException(String message) {
        super(message);
    }

    public OdinException(String message, Exception e) {
        super(message, e);
    }

    public OdinException(Exception e) {
        super(e);
    }
}
