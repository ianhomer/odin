package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.server.rest.domain.PersistablePattern;
import org.springframework.data.repository.CrudRepository;

/**
 * Pattern repository.
 */
public interface PatternRepository extends CrudRepository<PersistablePattern, Long> {
}
