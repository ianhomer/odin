package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.sequence.AbstractSequence;
import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Ticks;

/**
 * Metronome sequence configuration.
 */
public class Metronome extends AbstractSequence<Note> {
  private Note noteBarStart;
  private Note noteMidBar;

  /**
   * Create a metronome.
   */
  public Metronome() {
    noteBarStart = new DefaultNote();
    noteMidBar = new DefaultNote(64, noteBarStart.getVelocity() / 2);
    setTick(Ticks.HALF);
    setFlowName(MetronomeFlow.class.getName());
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
