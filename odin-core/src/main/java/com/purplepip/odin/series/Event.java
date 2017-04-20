package com.purplepip.odin.series;

/**
 * Series Event.
 */
public interface Event<A> {
  A getValue();

  /**
   * Get relative time.  The unit of time is dependent on implementation, e.g. it could be beats, bars, seconds,
   * or milliseconds.
   *
   * @return time
   */
  long getTime();
}
