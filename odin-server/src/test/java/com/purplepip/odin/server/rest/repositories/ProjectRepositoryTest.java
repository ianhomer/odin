package com.purplepip.odin.server.rest.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.server.rest.PersistableProjectBuilder;
import com.purplepip.odin.server.rest.domain.PersistableProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Project repository test.
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
public class ProjectRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ProjectRepository repository;

  private PersistableProject project;
  private PersistableProjectBuilder builder;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    project = new PersistableProject();
    builder = new PersistableProjectBuilder(new ProjectContainer(project));
    // TODO : Remove this delete all, DatabaseLoader should not execute for this test and
    // repository should be clean at start
    repository.deleteAll();
  }

  @Test
  public void testProject() throws OdinException {
    Iterable<PersistableProject> projects = repository.findAll();
    assertThat(projects.iterator().hasNext()).isFalse();
    builder.changeProgramTo("newProgram").addMetronome();
    entityManager.persist(project);
    project.setName("newProject");
    Project reloadedProject = repository.findByName("newProject");
    assertThat(reloadedProject).isNotNull();
    assertThat(reloadedProject.getChannels().iterator().next().getProgramName())
        .isEqualTo("newProgram");
  }
}
