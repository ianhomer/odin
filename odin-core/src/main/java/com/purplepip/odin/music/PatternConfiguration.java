package com.purplepip.odin.music;

import com.purplepip.odin.sequence.SequenceConfiguration;
import com.purplepip.odin.sequence.Tick;

/**
 * Pattern configuration.
 */
public class PatternConfiguration implements SequenceConfiguration {
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int pattern;
  private Note note;
  private Tick tick;

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

  public void setTick(Tick tick) {
    this.tick = tick;
  }

  public Tick getTick() {
    return tick;
  }

}
