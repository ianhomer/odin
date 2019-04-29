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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.purplepip.odin.api.rest.repositories.PerformanceRepository;
import com.purplepip.odin.store.StoreTest;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@StoreTest
public class PersistablePerformanceTest {
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
    assertThatThrownBy(() ->
        entityManager.flush())
        .hasCauseInstanceOf(ConstraintViolationException.class);
  }
}