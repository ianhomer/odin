/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.store.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.api.Application;
import com.purplepip.odin.api.rest.repositories.PerformanceRepository;
import com.purplepip.odin.api.rest.repositories.SequenceRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"noServices", "noAuditing", "noSequencer", "test"})
@ContextConfiguration(classes = Application.class)
public class PersistableSequenceTest {
  private static final String PERFORMANCE_NAME = "sequence-performance";
  private static final String SEQUENCE_NAME = "sequence";
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PerformanceRepository performanceRepository;

  @Autowired
  private SequenceRepository sequenceRepository;

  @Test
  public void testCreateAndDelete() {
    PersistablePerformance performance = new PersistablePerformance();
    performance.setName(PERFORMANCE_NAME);
    performanceRepository.save(performance);
    assertEquals(PERFORMANCE_NAME, performanceRepository.findByName(PERFORMANCE_NAME).getName());
    PersistableSequence sequence = new PersistableSequence();
    sequence.setName(SEQUENCE_NAME);
    sequence.setType("pattern");
    performance.addSequence(sequence);
    sequenceRepository.save(sequence);
    entityManager.flush();
    assertTrue("Sequence should have been created", anyMatchInPerformance());
    sequenceRepository.delete(sequence);
    assertFalse("Sequence should have been deleted", anyMatch());
    assertFalse("Sequence should have been deleted from performance", anyMatchInPerformance());
  }

  private boolean anyMatch() {
    return sequenceRepository.findByName(SEQUENCE_NAME) != null;
  }

  private boolean anyMatchInPerformance() {
    return performanceRepository.findByName(PERFORMANCE_NAME)
        .getSequences().stream()
        .anyMatch(s -> SEQUENCE_NAME.equals(s.getName()));
  }

}
