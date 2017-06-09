package com.purplepip.odin.server;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.server.rest.PersistableProjectBuilder;
import com.purplepip.odin.server.rest.domain.PersistableProject;
import com.purplepip.odin.server.rest.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Database Loader.
 */
@Component
@Slf4j
public class DatabaseLoader implements CommandLineRunner {
  @Autowired
  private ProjectContainer container;

  private ProjectRepository repository;

  @Autowired
  public DatabaseLoader(ProjectRepository repository) {
    this.repository = repository;
  }

  @Override
  public void run(String... strings) throws Exception {
    new PersistableProjectBuilder(container)
        .addMetronome()
        .withChannel(1).changeProgramTo("bird")
        .withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
        .withChannel(2).changeProgramTo("aahs")
        .withVelocity(20).withNote(42).addPattern(Ticks.BEAT, 15)
        .withChannel(9).changeProgramTo("TR-909")
        .withVelocity(100).withNote(62).addPattern(Ticks.BEAT, 2)
        .withVelocity(40).addPattern(Ticks.EIGHTH, 127)
        .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);

    repository.save((PersistableProject) container.getProject());
  }
}
