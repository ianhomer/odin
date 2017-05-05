package com.purplepip.odin.sequence.logic;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Tock;

/**
 * Sequence Event Provider.
 */
public interface Logic {
  Event<Note> getNextEvent(Tock tock);
}
