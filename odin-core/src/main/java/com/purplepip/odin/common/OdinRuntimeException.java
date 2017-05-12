package com.purplepip.odin.common;

/**
 * Odin runtime exception.
 */
public class OdinRuntimeException extends RuntimeException {
  public OdinRuntimeException(String message, Exception e) {
    super(message, e);
  }

  public OdinRuntimeException(String message) {
    super(message);
  }
}

