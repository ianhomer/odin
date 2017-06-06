package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.MutableSequence;

/**
 * Metronome sequence configuration.
 */
public interface Metronome extends MutableSequence {
  /**
   * Get note for the start of the bar.
   *
   * @return note
   */
  Note getNoteBarStart();

  void setNoteBarStart(Note note);

  /**
   * Get note for mid bar.
   *
   * @return note
   */
  Note getNoteMidBar();

  void setNoteMidBar(Note note);
}
