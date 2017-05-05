package com.purplepip.odin.server.rest;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.SequenceRuntime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

/**
 * SequenceRuntime repository.
 */
public interface SequenceRepository extends Repository<SequenceRuntime<Note>, Long> {
  Page<SequenceRuntime<Note>> findAll(Pageable pageable);
}
