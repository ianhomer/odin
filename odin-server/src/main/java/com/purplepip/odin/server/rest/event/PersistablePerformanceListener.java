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

package com.purplepip.odin.server.rest.event;

import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.PerformanceLoadListener;
import com.purplepip.odin.performance.PerformanceSaveListener;
import com.purplepip.odin.server.rest.repositories.PerformanceRepository;
import com.purplepip.odin.store.domain.PersistablePerformance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Performance container that contains a persistable performance.
 */
@Component
@Profile("!noStore")
@Slf4j
public class PersistablePerformanceListener implements
    PerformanceSaveListener, PerformanceLoadListener, InitializingBean {

  @Autowired
  private PerformanceContainer performanceContainer;

  @Autowired
  private PerformanceRepository performanceRepository;

  /**
   * Refresh container.
   */
  @Override
  public void onPerformanceLoad(PerformanceContainer container) {
    LOG.info("Reloading performance");
    container.setPerformance(performanceRepository.findByName(container.getName()));
  }

  @Override
  public void onPerformanceSave(Performance performance) {
    if (performance instanceof PersistablePerformance) {
      LOG.info("Saving performance");
      performanceRepository.save((PersistablePerformance) performance);
    }
  }

  @Override
  public void afterPropertiesSet() {
    performanceContainer.addSaveListener(this);
    performanceContainer.addLoadListener(this);
  }
}
