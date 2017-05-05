package com.purplepip.odin.music;

import com.purplepip.odin.sequence.AbstractSequence;
import com.purplepip.odin.sequence.Tick;

/**
 * MetronomeRuntime configuration.
 */
public class Metronome extends AbstractSequence {
  private Note noteBarStart;
  private Note noteMidBar;

  /**
   * Create a metronome.
   */
  public Metronome() {
    noteBarStart = new DefaultNote();
    noteMidBar = new DefaultNote(64, noteBarStart.getVelocity() / 2);
    setTick(Tick.HALF);
  }

  /**
   * Get note for the start of the bar.
   *
   * @return note
   */
  public Note getNoteBarStart() {
    return noteBarStart;
  }

  /**
   * Get note for mid bar.
   *
   * @return note
   */
  public Note getNoteMidBar() {
    return noteMidBar;
  }
}
