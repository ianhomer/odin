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

import com.purplepip.odin.api.rest.repositories.PerformanceRepository;
import com.purplepip.odin.api.rest.repositories.TriggerRepository;
import com.purplepip.odin.store.StoreTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@StoreTest
public class PersistableTriggerTest {
  private static final String PERFORMANCE_NAME = "trigger-performance";
  private static final String TRIGGER_NAME = "trigger";

  @Autowired
  private PerformanceRepository performanceRepository;

  @Autowired
  private TriggerRepository triggerRepository;

  @Test
  public void testCreateAndDelete() {
    PersistablePerformance performance = new PersistablePerformance();
    performance.setName(PERFORMANCE_NAME);
    performanceRepository.save(performance);
    PersistableTrigger trigger = new PersistableTrigger();
    trigger.setName(TRIGGER_NAME);
    trigger.setType("default");
    performance.addTrigger(trigger);
    triggerRepository.save(trigger);
    assertEquals("Trigger should have been created", 1, countInPerformance());
    triggerRepository.delete(trigger);
    assertEquals("Trigger should have been deleted", 0, count());
    assertEquals("Trigger should have been deleted from performance", 0, countInPerformance());
  }

  private long count() {
    return triggerRepository.findByName(TRIGGER_NAME).size();
  }

  private long countInPerformance() {
    return performanceRepository.findByName(PERFORMANCE_NAME)
        .getTriggers().stream()
        .filter(s -> TRIGGER_NAME.equals(s.getName())).count();
  }
}
