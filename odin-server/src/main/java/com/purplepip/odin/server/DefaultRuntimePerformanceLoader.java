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

import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.PerformanceLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Slf4j
@Order(4)
public class DefaultRuntimePerformanceLoader implements CommandLineRunner {
  @Autowired
  private PerformanceLoader loader;

  @Autowired
  private PerformanceContainer container;

  @Override
  public void run(String... args) throws Exception {
    container.setPerformance(new GroovePerformance());
    //loader.load(new ClassUri(GroovePerformance.class).getUri());
    LOG.info("Loaded performance from {}", DefaultRuntimePerformanceLoader.class.getName());
  }
}
