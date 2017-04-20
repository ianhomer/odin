package com.purplepip.odin.common;

/**
 * Custom runtime MIDI exception.
 */
public class OdinRuntimeException extends RuntimeException {
  public OdinRuntimeException(String message) {
    super(message);
  }
}
