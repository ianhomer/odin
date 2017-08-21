package com.purplepip.odin.server.rest.repositories;

import static com.purplepip.odin.store.domain.Persistables.newLayer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.store.PersistableProjectBuilder;
import com.purplepip.odin.store.domain.PersistableLayer;
import com.purplepip.odin.store.domain.PersistableProject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Project repository test.
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"test", "noServices"})
@Slf4j
public class LayerRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private LayerRepository repository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private LayerRepository layerRepository;

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
    for (Layer layer : Lists.newArrayList(project.getLayers())) {
      LOG.debug("Persisting {}", layer);
      entityManager.persist(layer);
    }
    layers = repository.findByName("test");
    assertThat(1, equalTo(layers.size()));
    Layer layer = layers.get(0);
    assertThat("test", equalTo(layer.getName()));
  }

  @Test
  public void testLayoutRemove() throws OdinException, ExecutionException, InterruptedException {
    PersistableProject project = new PersistableProject();
    project.setName("test-project");
    projectRepository.save(project);
    layerRepository.save(newLayer(project,"test-layer"));
    Assertions.assertThat(project.getLayers().size()).isEqualTo(1);
    projectRepository.save(project);

    Project existingProject = projectRepository.findByName("test-project");
    Assertions.assertThat(existingProject.getLayers().size()).isEqualTo(1);
    layerRepository.delete((PersistableLayer) existingProject.getLayers().iterator().next());

    existingProject = projectRepository.findByName("test-project");
    Assertions.assertThat(existingProject.getLayers().size()).isEqualTo(0);
  }

  @Test
  public void testCreateMultipleLayers() throws OdinException {
    PersistableProject project = new PersistableProject();
    project.setName("test-project");
    projectRepository.save(project);
    layerRepository.save(newLayer(project,"test-layer1"));
    layerRepository.save(newLayer(project,"test-layer2"));
    Assertions.assertThat(project.getLayers().size()).isEqualTo(2);
    projectRepository.save(project);

    Project existingProject = projectRepository.findByName("test-project");
    Assertions.assertThat(existingProject.getLayers().size()).isEqualTo(2);
  }
}