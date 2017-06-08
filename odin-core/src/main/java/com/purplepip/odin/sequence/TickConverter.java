package com.purplepip.odin.sequence;

/**
 * Tick converter.
 */
public interface TickConverter {
  Tick getOutputTick();

  long convert(long time);

  long convertDuration(long time, long duration);
}
