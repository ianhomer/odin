package com.purplepip.odin.sequence;

import com.purplepip.odin.music.Note;

/**
 * Sequence Event Provider.
 */
public interface SequenceEventProvider {
  Event<Note> getNextEvent(Tock tock);
}
