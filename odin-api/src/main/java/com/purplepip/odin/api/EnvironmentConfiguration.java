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

import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.system.Environments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Component that loads the runtime configuration beans (that are not needed for test profile).
 */
@Component
@Profile("!test")
@Order(2)
public class EnvironmentConfiguration {
  /**
   * Create new environment.
   *
   * @return environment
   */
  @Bean
  public Environment odinEnvironment() {
    return Environments.newEnvironment();
  }
}
