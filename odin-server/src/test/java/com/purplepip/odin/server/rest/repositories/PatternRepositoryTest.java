package com.purplepip.odin.server.rest.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.server.rest.PersistableSequenceBuilder;
import java.util.List;
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
public class PatternRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PatternRepository repository;

  private TransientProject project;
  private PersistableSequenceBuilder builder;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    project = new TransientProject();
    builder = new PersistableSequenceBuilder(project);
    // TODO : Remove this delete all, DatabaseLoader should not execute for this test and
    // repository should be clean at start
    repository.deleteAll();
  }

  @Test
  public void testPattern() throws OdinException {
    List<Pattern> patterns = repository.findByChannel(0);
    assertThat(patterns.size()).isEqualTo(0);

    builder.addPattern(Ticks.BEAT, 9876);
    for (Sequence<Note> sequence : project.getSequences()) {
      entityManager.persist(sequence);
    }
    patterns = repository.findByChannel(0);
    assertThat(patterns.size()).isEqualTo(1);
    Pattern pattern = patterns.get(0);
    assertThat(pattern.getBits()).isEqualTo(9876);
    assertThat(pattern.getTick().equals(Ticks.BEAT));
  }
}