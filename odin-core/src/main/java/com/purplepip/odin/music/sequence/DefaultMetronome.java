package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.AbstractSequence;

/**
 * Default implementation of the Metronome.
 */
public class DefaultMetronome extends AbstractSequence<Note> implements Metronome {
  private Note noteBarStart;
  private Note noteMidBar;

  /**
   * Get note for the start of the bar.
   *
   * @return note
   */
  @Override
  public Note getNoteBarStart() {
    return noteBarStart;
  }

  @Override
  public void setNoteBarStart(Note note) {
    noteBarStart = note;
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

  @Override
  public void setNoteMidBar(Note note) {
    noteMidBar = note;
  }
}
