package com.purplepip.odin.sequence;

/**
 * Beats per minute provider.
 */
public interface BeatsPerMinute {
  int getBeatsPerMinute();

  long getMicroSecondsPerBeat();
}
