package com.purplepip.odin.music;

/**
 * Note.
 */
public interface Note {
  /**
   * Get number for the note following the MIDI standard, C0 = 0, C5 = 60.
   *
   * @return int
   */
  int getNumber();

  /**
   * Get the note velocity from 0 to 127.
   *
   * @return int
   */
  int getVelocity();

  /**
   * Get the note duration.  Time units are dependent on the context.
   *
   * @return long
   */
  long getDuration();


}
