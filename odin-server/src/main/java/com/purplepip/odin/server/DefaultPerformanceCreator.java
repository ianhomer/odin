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

import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.server.rest.repositories.PerformanceRepository;
import com.purplepip.odin.store.domain.PersistablePerformance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!noStore")
@Slf4j
@Order(5)
public class DefaultPerformanceCreator implements CommandLineRunner {
  private static final String DEFAULT_PERFORMANCE_NAME = "defaultPerformance";

  @Autowired
  private PerformanceRepository performanceRepository;

  @Autowired
  private PerformanceContainer performanceContainer;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    if (performanceRepository.count() == 0) {
      PersistablePerformance performance = new PersistablePerformance();
      performance.setName(DEFAULT_PERFORMANCE_NAME);
      performanceRepository.save(performance);
      performanceContainer.setPerformance(performance);
      LOG.info("Created default performance");
    } else {
      /*
       * Otherwise load the first we'll find.
       * //TODO : Control which performance to start with.
       */
      performanceContainer.setPerformance(
          performanceRepository.findAll().iterator().next()
      );
    }
    performanceContainer.apply();
  }
}
