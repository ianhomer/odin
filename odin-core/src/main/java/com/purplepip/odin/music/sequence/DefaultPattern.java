package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.AbstractSequence;

/**
 * Default implementation of pattern.
 */
public class DefaultPattern extends AbstractSequence implements Pattern {
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int bits;
  private Note note;

  @Override
  public void setBits(int bits) {
    this.bits = bits;
  }

  @Override
  public int getBits() {
    return bits;
  }

  @Override
  public void setNote(Note note) {
    this.note = note;
  }

  @Override
  public Note getNote() {
    return note;
  }
}
