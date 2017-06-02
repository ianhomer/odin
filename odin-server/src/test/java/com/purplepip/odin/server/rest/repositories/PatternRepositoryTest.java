package com.purplepip.odin.server.rest.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.server.rest.PersistableSequenceBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern repository test.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class PatternRepositoryTest {
  private static final Logger LOG = LoggerFactory.getLogger(PatternRepositoryTest.class);

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PatternRepository repository;

  private TransientProject project;
  private PersistableSequenceBuilder builder;

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
      LOG.info("Saving {}", sequence);
      entityManager.persist(sequence);
    }
    patterns = repository.findByChannel(0);
    for (Sequence<Note> sequence : project.getSequences()) {
      LOG.info("Saved sequence {}", sequence);
    }
    assertThat(patterns.size()).isEqualTo(1);
    Pattern pattern = patterns.get(0);
    assertThat(pattern.getBits()).isEqualTo(9876);
    assertThat(pattern.getTick().equals(Ticks.BEAT));
  }
}