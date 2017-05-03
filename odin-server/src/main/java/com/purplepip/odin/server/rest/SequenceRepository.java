package com.purplepip.odin.server.rest;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Sequence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

/**
 * Sequence repository.
 */
public interface SequenceRepository extends Repository<Sequence<Note>, Long> {
  Page<Sequence<Note>> findAll(Pageable pageable);
}
