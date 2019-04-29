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

package com.purplepip.odin.store;

import static org.junit.Assert.assertTrue;

import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.demo.DemoLoaderPerformance;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.store.domain.PersistablePerformance;
import com.purplepip.odin.store.domain.PersistableThing;
import com.purplepip.odin.store.domain.PersistableTick;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@StoreTest
public class PersistablePerformanceContainerTest {
  @Autowired private PerformanceContainer container;

  @Test
  public void testSetNonPersistablePerformance() {
    container.mixin(new GroovePerformance());
    container.mixin(new DemoLoaderPerformance());
    Performance performance = container.getPerformance();
    assertTrue(performance instanceof PersistablePerformance);
    assertTrue(performance.getLayers().iterator().next() instanceof PersistableThing);
    assertTrue(performance.getSequences().iterator().next() instanceof PersistableThing);
    assertTrue(performance.getTriggers().iterator().next() instanceof PersistableThing);
    assertTrue(performance.getChannels().iterator().next() instanceof PersistableThing);

    Optional<SequenceConfiguration> crashOptional =
        performance
            .getSequences()
            .stream()
            .filter(sequence -> "crash".equals(sequence.getName()))
            .findFirst();
    assertTrue(crashOptional.isPresent());
    SequenceConfiguration crash = crashOptional.get();
    assertTrue(crash instanceof PersistableThing);
    assertTrue("Tick should be a persistable tick", crash.getTick() instanceof PersistableTick);
    assertTrue(
        "Some properties should exist in the crash sequence", crash.getPropertyNames().count() > 0);
  }
}
