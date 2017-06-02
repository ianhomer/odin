package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.server.rest.domain.PersistablePattern;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Pattern repository.
 */
public interface PatternRepository extends CrudRepository<PersistablePattern, Long> {
  List<Pattern> findByChannel(int channel);
}
