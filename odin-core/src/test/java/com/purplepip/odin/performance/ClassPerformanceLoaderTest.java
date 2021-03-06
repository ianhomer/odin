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

package com.purplepip.odin.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.demo.DemoPerformances;
import com.purplepip.odin.demo.SimplePerformance;
import java.net.URI;
import org.junit.jupiter.api.Test;

class ClassPerformanceLoaderTest {
  @Test
  void testLoad() {
    DefaultPerformanceContainer container = new DefaultPerformanceContainer();
    PerformanceLoader loader = new ClassPerformanceLoader(new SimplePerformance(), container);
    URI uri = new ClassUri(SimplePerformance.class, true).getUri();
    loader.load(uri);
    assertEquals("com/purplepip/odin/demo/SimplePerformance",
        container.getPerformance().getName());
  }

  @Test
  void testCanLoad() {
    URI uri = new ClassUri(SimplePerformance.class).getUri();
    assertTrue(new ClassPerformanceLoader(new SimplePerformance()).canLoad(uri));
    uri = new ClassUri(SimplePerformance.class, true).getUri();
    assertTrue(new ClassPerformanceLoader(new SimplePerformance()).canLoad(uri));
  }

  @Test
  void testDemoPerformancesLoad() {
    DefaultPerformanceContainer container = new DefaultPerformanceContainer();
    PerformanceLoader loader = new ClassPerformanceLoader(new DemoPerformances().getPerformances(),
        container);
    URI uri = new ClassUri(SimplePerformance.class, true).getUri();
    loader.load(uri);
    assertEquals("com/purplepip/odin/demo/SimplePerformance",
        container.getPerformance().getName());
  }
}