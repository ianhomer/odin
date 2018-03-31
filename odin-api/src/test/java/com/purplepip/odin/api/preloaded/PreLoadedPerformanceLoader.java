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

package com.purplepip.odin.api.preloaded;

import com.purplepip.odin.api.PerformanceImporter;
import com.purplepip.odin.demo.GroovePerformance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This loader loads performances for some test cases where we need test data data loaded.
 */
@Component
@Profile("testPreLoaded")
@Slf4j
@Order(4)
public class PreLoadedPerformanceLoader implements CommandLineRunner {
  @Autowired
  private PerformanceImporter importer;

  @Override
  public void run(String... args) {
    importer.load(new GroovePerformance());
  }
}
