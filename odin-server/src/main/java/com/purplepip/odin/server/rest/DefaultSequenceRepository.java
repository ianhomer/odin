package com.purplepip.odin.server.rest;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Sequence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Default sequence repository.
 */
@Component
public class DefaultSequenceRepository implements SequenceRepository {

  @Override
  public Page<Sequence<Note>> findAll(Pageable pageable) {
    return new PageImpl(new ArrayList<Sequence<Note>>());
  }
}
