package com.purplepip.odin.sequence;

/**
 * Tick converter.
 */
public interface TickConverter {
  Tick getOutputTick();

  long convert(long time);

  /**
   * Convert duration at the given time.  Note that duration can be variable over time so
   * to accurately calculate duration the time at which the duration is to be calculated must
   * be provided.
   *
   * @param time time for when duration should be calculated
   * @param duration duration to convert
   * @return converted duration
   */
  long convertDuration(long time, long duration);
}
