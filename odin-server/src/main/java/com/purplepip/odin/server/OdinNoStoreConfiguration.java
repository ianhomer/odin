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
import com.purplepip.odin.performance.TransientPerformance;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration to disable storage configuration.
 */
@Configuration
@EnableAutoConfiguration(
    exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class}
)
@Profile("noStore")
public class OdinNoStoreConfiguration {
  /**
   * Create performance container bean NOT backed by a persistent store.
   *
   * @return performance container
   */
  @Bean
  public PerformanceContainer performanceContainer() {
    PerformanceContainer performanceContainer = new PerformanceContainer();
    performanceContainer.setPerformance(new TransientPerformance());
    return performanceContainer;
  }
}
