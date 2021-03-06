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

import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.PerformanceContainer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for system persistence.
 */
@Configuration
@EntityScan("com.purplepip.odin.store.domain")
@Profile("!noStore")
public class PersistenceConfiguration {
  /**
   * Create the performance container used persistence configuration.
   *
   * @return performance container.
   */
  @Bean
  public PerformanceContainer performanceContainer() {
    return new DefaultPerformanceContainer();
  }
}
