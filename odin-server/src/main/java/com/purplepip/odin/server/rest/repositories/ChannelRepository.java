package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.server.rest.domain.PersistableChannel;
import org.springframework.data.repository.CrudRepository;

/**
 * Channel repository.
 */
public interface ChannelRepository extends CrudRepository<PersistableChannel, Long> {
}
