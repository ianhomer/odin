package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.server.rest.domain.PersistableMetronome;
import org.springframework.data.repository.CrudRepository;

/**
 * Metronome repository.
 */
public interface MetronomeRepository extends CrudRepository<PersistableMetronome, Long> {
}
