package com.purplepip.odin.api.rest.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.store.PersistablePerformanceBuilder;
import com.purplepip.odin.store.StoreTest;
import com.purplepip.odin.store.domain.PersistablePerformance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Performance repository test.
 */
@RunWith(SpringRunner.class)
@StoreTest
public class PerformanceRepositoryTest {
  @Autowired
  private PerformanceRepository repository;

  private PersistablePerformance performance;
  private PersistablePerformanceBuilder builder;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    performance = new PersistablePerformance();
    builder = new PersistablePerformanceBuilder(new DefaultPerformanceContainer(performance));
  }

  @Test
  public void testPerformance() {
    Iterable<PersistablePerformance> performances = repository.findAll();
    assertThat(performances.iterator().hasNext()).isTrue();
    assertThat(performances.iterator().next().getChannels()).isEmpty();
    builder.changeProgramTo("new-program").withName("new-metronome").addMetronome();
    repository.save(performance);
    performance.setName("new-performance");
    Performance reloadedPerformance = repository.findByName("new-performance");
    assertThat(reloadedPerformance).isNotNull();
    assertThat(reloadedPerformance.getChannels().iterator().next().getProgramName())
        .isEqualTo("new-program");
    assertThat(reloadedPerformance.getSequences().iterator()
        .next().getTick().getFactor().getDenominator())
        .isEqualTo(2);
  }
}
