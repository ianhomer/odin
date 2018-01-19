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

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Sets;
import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.PerformanceLoader;
import com.purplepip.odin.server.rest.repositories.ChannelRepository;
import com.purplepip.odin.server.rest.repositories.PerformanceRepository;
import com.purplepip.odin.store.domain.PersistableChannel;
import com.purplepip.odin.store.domain.PersistablePerformance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"test", "noServices", "noAuditing"})
@ContextConfiguration
public class PersistablePerformanceLoaderTest {
  @Autowired
  private PerformanceContainer container;

  @Autowired
  private PerformanceLoader loader;

  @Autowired
  private PerformanceRepository performanceRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @Transactional
  public void testLoad() throws Exception {
    save(new GroovePerformance());
    //save(new SimplePerformance());
    //entityManager.flush();
    loader.load(new ClassUri(GroovePerformance.class).getUri());
    assertEquals("com/purplepip/odin/demo/GroovePerformance",
        container.getPerformance().getName());
  }

  private void save(Performance performance) {
    PersistablePerformance persistablePerformance = new PersistablePerformance();
    persistablePerformance.setName(performance.getName());
    persistablePerformance = performanceRepository.save(persistablePerformance);
    persistablePerformance.mixin(performance);
    Sets.newHashSet(persistablePerformance.getChannels())
        .forEach(channel -> channelRepository.save((PersistableChannel) channel));
    persistablePerformance.getLayers().clear();
    persistablePerformance.getTriggers().clear();
    persistablePerformance.getSequences().clear();
    performanceRepository.save(persistablePerformance);
  }
}