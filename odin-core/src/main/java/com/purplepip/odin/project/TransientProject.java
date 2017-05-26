package com.purplepip.odin.project;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Sequence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A light weight non-persistable in memory project implementation.
 */
public class TransientProject extends AbstractProject {
  private static final Logger LOG = LoggerFactory.getLogger(TransientProject.class);

  private Set<Sequence<Note>> sequences = new HashSet<>();

  @Override
  public void addSequence(Sequence<Note> sequence) {
    LOG.debug("Adding sequence {} with time units {}",
        sequence.getClass().getSimpleName(),
        sequence.getTick().getClass().getSimpleName());
    sequences.add(sequence);
  }

  @Override
  public Iterable<Sequence<Note>> getSequences() {
    return Collections.unmodifiableSet(sequences);
  }
}
