package com.purplepip.odin.server.rest.repositories;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Layer;
import com.purplepip.odin.store.PersistableProjectBuilder;
import com.purplepip.odin.store.domain.PersistableProject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Project repository test.
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ContextConfiguration
@Slf4j
public class LayerRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private LayerRepository repository;

  @Autowired
  private ProjectRepository projectRepository;

  private PersistableProject project;
  private PersistableProjectBuilder builder;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    project = new PersistableProject();
    projectRepository.save(project);
    builder = new PersistableProjectBuilder(new ProjectContainer(project));
  }

  @Test
  public void testProject() throws OdinException {
    List<Layer> layers = Lists.newArrayList(repository.findAll());
    assertThat(0, equalTo(layers.size()));

    builder.addLayer("test");
    for (Layer layer : project.getLayers()) {
      LOG.debug("Persisting {}", layer);
      entityManager.persist(layer);
    }
    layers = repository.findByName("test");
    assertThat(1, equalTo(layers.size()));
    Layer layer = layers.get(0);
    assertThat("test", equalTo(layer.getName()));
  }
}
