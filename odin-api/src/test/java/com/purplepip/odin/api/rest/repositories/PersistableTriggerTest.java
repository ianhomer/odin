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

package com.purplepip.odin.api.rest.repositories;

import static com.purplepip.odin.store.domain.TestPersistables.newPerformance;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.store.StoreTest;
import com.purplepip.odin.store.domain.PersistableTrigger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@StoreTest
public class PersistableTriggerTest {
  private static final String TRIGGER_NAME = PersistableTrigger.class.getSimpleName();
  private static final String PERFORMANCE_NAME = TRIGGER_NAME + "-performance";

  @Autowired
  private PerformanceRepository performanceRepository;

  @Autowired
  private TriggerRepository repository;

  @Test
  public void testCreateAndDelete() {
    Performance performance = performanceRepository.save(newPerformance(PERFORMANCE_NAME));
    PersistableTrigger trigger = new PersistableTrigger();
    trigger.setName(TRIGGER_NAME);
    trigger.setType("default");
    performance.addTrigger(trigger);
    repository.save(trigger);
    assertEquals("Trigger should have been created", 1L, countInPerformance());
    repository.delete(trigger);
    assertEquals("Trigger should have been deleted", 0L, count());
    assertEquals("Trigger should have been deleted from performance", 0L, countInPerformance());
  }

  private long count() {
    return repository.findByName(TRIGGER_NAME).size();
  }

  private long countInPerformance() {
    return performanceRepository.findByName(PERFORMANCE_NAME)
        .getTriggers().stream()
        .filter(s -> TRIGGER_NAME.equals(s.getName())).count();
  }
}
