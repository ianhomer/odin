package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.sequence.AbstractSequence;
import com.purplepip.odin.sequence.Ticks;

/**
 * Default implementation of the Metronome.
 */
public class DefaultMetronome extends AbstractSequence<Note> implements Metronome {
  private Note noteBarStart;
  private Note noteMidBar;

  /**
   * Create a metronome.
   */
  public DefaultMetronome() {
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
  @Override
  public Note getNoteBarStart() {
    return noteBarStart;
  }

  /**
   * Get note for mid bar.
   *
   * @return note
   */
  @Override
  public Note getNoteMidBar() {
    return noteMidBar;
  }
}
