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

package com.purplepip.odin.server;

import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.server.rest.repositories.PerformanceRepository;
import com.purplepip.odin.store.domain.PersistablePerformance;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"noServices", "noAuditing"})
public class PersistablePerformanceTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PerformanceRepository performanceRepository;

  @Test
  public void testUniqueNameConstraint() {
    PersistablePerformance persistablePerformance1 = new PersistablePerformance();
    persistablePerformance1.setName("test");
    performanceRepository.save(persistablePerformance1);
    assertEquals("test", performanceRepository.findByName("test").getName());
    PersistablePerformance persistablePerformance2 = new PersistablePerformance();
    persistablePerformance2.setName("test");
    performanceRepository.save(persistablePerformance2);
    thrown.expectCause(isA(ConstraintViolationException.class));
    entityManager.flush();
  }
}