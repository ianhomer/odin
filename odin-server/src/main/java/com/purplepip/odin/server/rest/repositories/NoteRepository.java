package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.server.rest.domain.PersistableNote;
import org.springframework.data.repository.CrudRepository;

/**
 * Note repository.
 */
public interface NoteRepository extends CrudRepository<PersistableNote, Long> {
}
