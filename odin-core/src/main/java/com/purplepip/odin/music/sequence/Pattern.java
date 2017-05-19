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
  private int patternAsInt;
  private Note note;

  public void setPattern(int patternAsInt) {
    this.patternAsInt = patternAsInt;
  }

  public int getPattern() {
    return patternAsInt;
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
