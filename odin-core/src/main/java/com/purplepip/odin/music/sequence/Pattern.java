package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.MutableSequence;

/**
 * Pattern sequence configuration.
 */
public interface Pattern extends MutableSequence {
  void setBits(int bits);

  int getBits();

  void setNote(Note note);

  Note getNote();
}
