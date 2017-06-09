package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.server.rest.domain.PersistableSequence;
import org.springframework.data.repository.CrudRepository;

/**
 * Sequence repository.
 */
public interface SequenceRepository extends CrudRepository<PersistableSequence, Long>  {
}
