package com.purplepip.odin.server.rest.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.store.PersistablePerformanceBuilder;
import com.purplepip.odin.store.domain.PersistablePerformance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Performance repository test.
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"test", "noServices"})
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
    builder = new PersistablePerformanceBuilder(new PerformanceContainer(performance));
  }

  @Test
  public void testPerformance() throws OdinException {
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
