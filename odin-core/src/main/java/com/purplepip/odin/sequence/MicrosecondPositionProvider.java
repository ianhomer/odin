package com.purplepip.odin.sequence;

/**
 * Provides the microsecond position.
 */
@FunctionalInterface
public interface MicrosecondPositionProvider {
  long getMicrosecondPosition();
}
