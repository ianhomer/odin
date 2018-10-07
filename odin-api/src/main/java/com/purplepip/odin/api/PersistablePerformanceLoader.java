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

package com.purplepip.odin.api;

import com.purplepip.odin.api.rest.repositories.PerformanceRepository;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.performance.AbstractPerformanceLoader;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Component that loads performances.
 */
@Component
@Slf4j
@Profile("!noStore")
public class PersistablePerformanceLoader extends AbstractPerformanceLoader {
  @Autowired
  private PerformanceContainer container;

  @Autowired
  private PerformanceRepository performanceRepository;

  @Override
  public void load(URI performanceUri) {
    String performanceName = performanceUri.getSchemeSpecificPart();
    Performance performance = performanceRepository.findByName(performanceName);
    if (performance == null) {
      throw new OdinRuntimeException("Cannot find performance " + performanceName);
    }
    container.setPerformance(overlay(performance));
    container.apply();
    LOG.info("Loaded performance {}", performanceUri);
  }

  @Override
  public boolean canLoad(URI performanceUri) {
    return performanceUri.getScheme() == null;
  }
}
