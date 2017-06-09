package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.project.Project;
import com.purplepip.odin.server.rest.domain.PersistableProject;
import org.springframework.data.repository.CrudRepository;

/**
 * Project repository.
 */
public interface ProjectRepository extends CrudRepository<PersistableProject, Long> {
  Project findByName(String name);
}
