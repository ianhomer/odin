package com.purplepip.odin.server.rest.repositories;

import static com.purplepip.odin.store.domain.TestPersistables.newLayer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.store.PersistablePerformanceBuilder;
import com.purplepip.odin.store.domain.PersistableLayer;
import com.purplepip.odin.store.domain.PersistablePerformance;
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
 * Performance repository test.
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
  private PerformanceRepository performanceRepository;

  @Autowired
  private LayerRepository layerRepository;

  private PersistablePerformance performance;
  private PersistablePerformanceBuilder builder;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    performance = new PersistablePerformance();
    performanceRepository.save(performance);
    builder = new PersistablePerformanceBuilder(new PerformanceContainer(performance));
  }

  @Test
  public void testPerformance() throws OdinException {
    List<Layer> layers = Lists.newArrayList(repository.findAll());
    assertThat(0, equalTo(layers.size()));

    builder.addLayer("test");
    for (Layer layer : Lists.newArrayList(performance.getLayers())) {
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
    PersistablePerformance performance = new PersistablePerformance();
    performance.setName("test-performance");
    performanceRepository.save(performance);
    layerRepository.save(newLayer(performance,"test-layer"));
    Assertions.assertThat(performance.getLayers().size()).isEqualTo(1);
    performanceRepository.save(performance);

    Performance existingPerformance = performanceRepository.findByName("test-performance");
    Assertions.assertThat(existingPerformance.getLayers().size()).isEqualTo(1);
    layerRepository.delete((PersistableLayer) existingPerformance.getLayers().iterator().next());

    existingPerformance = performanceRepository.findByName("test-performance");
    Assertions.assertThat(existingPerformance.getLayers().size()).isEqualTo(0);
  }

  @Test
  public void testCreateMultipleLayers() throws OdinException {
    PersistablePerformance performance = new PersistablePerformance();
    performance.setName("test-performance");
    performanceRepository.save(performance);
    layerRepository.save(newLayer(performance,"test-layer1"));
    layerRepository.save(newLayer(performance,"test-layer2"));
    Assertions.assertThat(performance.getLayers().size()).isEqualTo(2);
    performanceRepository.save(performance);

    Performance existingPerformance = performanceRepository.findByName("test-performance");
    Assertions.assertThat(existingPerformance.getLayers().size()).isEqualTo(2);
  }
}