package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.logic.PatternLogic;
import com.purplepip.odin.sequence.AbstractSequence;

/**
 * Pattern sequence configuration.
 */
public class Pattern extends AbstractSequence {
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int pattern;
  private Note note;

  public void setPattern(int pattern) {
    this.pattern = pattern;
  }

  public int getPattern() {
    return pattern;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public Note getNote() {
    return note;
  }

  @Override
  public PatternLogic getLogic() {
    return new PatternLogic(this);
  }
}
