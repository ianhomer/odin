package com.purplepip.odin.project;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Sequence;

/**
 * An Odin Project that stores configuration and state of the given runtime.
 */
public interface Project {
  void addSequence(Sequence<Note> sequence);

  Iterable<Sequence<Note>> getSequences();
}
