package com.purplepip.odin.server.rest;

import org.springframework.data.repository.CrudRepository;

/**
 * SequenceRuntime repository.
 */
public interface SequenceRepository extends CrudRepository<Pattern, Long> {
}
