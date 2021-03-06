package com.purplepip.odin.api.rest.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.store.PersistablePerformanceBuilder;
import com.purplepip.odin.store.StoreTest;
import com.purplepip.odin.store.domain.PersistablePerformance;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Pattern repository test.
 */
@ExtendWith(SpringExtension.class)
@StoreTest
@Slf4j
public class SequenceRepositoryTest {
  private PersistablePerformance performance;
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private SequenceRepository repository;
  @Autowired
  private PerformanceRepository performanceRepository;
  private PersistablePerformanceBuilder builder;

  /**
   * Set up.
   */
  @BeforeEach
  public void setUp() {
    performance = new PersistablePerformance();
    performanceRepository.save(performance);
    builder = new PersistablePerformanceBuilder(new DefaultPerformanceContainer(performance));
  }

  @Test
  public void testSequence() {
    List<SequenceConfiguration> sequences = repository.findByChannel(0);
    assertThat(sequences.size()).isEqualTo(0);

    builder
        .withName("test-pattern")
        .withNote(58).withOffset(4)
        .addPattern(Ticks.BEAT, 9876);
    for (SequenceConfiguration sequence : performance.getSequences()) {
      LOG.debug("Persisting {}", sequence);
      entityManager.persist(sequence);
    }
    sequences = repository.findByChannel(0);
    assertThat(sequences.size()).isEqualTo(1);
    SequenceConfiguration pattern = sequences.get(0);
    assertThat(pattern.getProperty("bits")).isEqualTo("9876");
    assertThat(pattern.getTick().getTimeUnit()).isEqualTo(Ticks.BEAT.getTimeUnit());
    assertThat(pattern.getTick().getFactor().getDenominator()).isEqualTo(1);
    assertThat(pattern.getProperty("note.number")).isEqualTo("58");
    assertThat(pattern.getOffset()).isEqualTo(Wholes.valueOf(4));
  }
}