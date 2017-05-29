package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.sequence.AbstractSequence;

/**
 * Pattern sequence configuration.
 */
public class Pattern extends AbstractSequence<Note> {
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int bits;
  private Note note;

  public Pattern() {
    setFlowName(PatternFlow.class.getName());
  }

  public void setBits(int bits) {
    this.bits = bits;
  }

  public int getBits() {
    return bits;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public Note getNote() {
    return note;
  }
}
