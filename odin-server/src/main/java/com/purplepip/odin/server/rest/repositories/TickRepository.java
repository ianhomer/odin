package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.server.rest.domain.PersistableTick;
import org.springframework.data.repository.CrudRepository;

/**
 * Tick repository.
 */
public interface TickRepository extends CrudRepository<PersistableTick, Long> {
}
