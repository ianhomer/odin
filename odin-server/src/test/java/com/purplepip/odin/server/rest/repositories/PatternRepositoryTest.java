package com.purplepip.odin.server.rest.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.tick.Ticks;
import com.purplepip.odin.store.PersistableProjectBuilder;
import com.purplepip.odin.store.domain.PersistableProject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Pattern repository test.
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Slf4j
public class PatternRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PatternRepository repository;

  @Autowired
  private ProjectRepository projectRepository;

  PersistableProject project;
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
  public void testPattern() throws OdinException {
    List<Pattern> patterns = repository.findByChannel(0);
    assertThat(patterns.size()).isEqualTo(0);

    builder
        .withName("test-pattern")
        .withProperty("offset", "4")
        .withProperty("note.number", "58")
        .addPattern(Ticks.BEAT, 9876);
    for (Sequence sequence : project.getSequences()) {
      LOG.debug("Persisting {}", sequence);
      entityManager.persist(sequence);
    }
    patterns = repository.findByChannel(0);
    assertThat(patterns.size()).isEqualTo(1);
    Pattern pattern = patterns.get(0);
    assertThat(pattern.getBits()).isEqualTo(9876);
    assertThat(pattern.getOffset()).isEqualTo(4);
    assertThat(pattern.getTick().getTimeUnit()).isEqualTo(Ticks.BEAT.getTimeUnit());
    assertThat(pattern.getTick().getFactor().getDenominator()).isEqualTo(1);
    assertThat(pattern.getNote().getNumber()).isEqualTo(58);
    assertThat(pattern.getProperty("offset")).isEqualTo("4");
    assertThat(pattern.getProperty("note.number")).isEqualTo("58");
  }
}