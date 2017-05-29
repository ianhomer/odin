package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.sequence.AbstractSequence;

/**
 * Pattern sequence configuration.
 */
public class Pattern extends AbstractSequence {
  public Pattern() {
    setFlowName(PatternFlow.class.getName());
  }

  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int bit;
  private Note note;

  public void setBit(int bit) {
    this.bit = bit;
  }

  public int getBit() {
    return bit;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public Note getNote() {
    return note;
  }
}
