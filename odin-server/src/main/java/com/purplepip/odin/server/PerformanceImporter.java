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

import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.server.rest.repositories.PerformanceRepository;
import com.purplepip.odin.store.domain.PersistablePerformance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!noStore")
public class PerformanceImporter {
  @Autowired
  private PerformanceRepository performanceRepository;

  /**
   * Load performance into the repository.
   *
   * @param performance performance to load
   */
  public void load(Performance performance) {
    PersistablePerformance persistablePerformance = new PersistablePerformance();
    persistablePerformance.setName(performance.getName());
    persistablePerformance.mixin(performance);
    performanceRepository.save(persistablePerformance);
  }
}
