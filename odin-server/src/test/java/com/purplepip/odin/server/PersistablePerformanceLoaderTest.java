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

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.demo.SimplePerformance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.PerformanceLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"test", "noServices", "noAuditing"})
@ContextConfiguration
public class PersistablePerformanceLoaderTest {
  @Autowired
  private PerformanceContainer container;

  @Autowired
  private PerformanceLoader loader;

  @Autowired
  private PerformanceImporter importer;

  @Test
  @Transactional
  public void testLoad() {
    importer.load(new GroovePerformance());
    importer.load(new SimplePerformance());
    loader.load(new ClassUri(GroovePerformance.class).getUri());
    assertEquals("com/purplepip/odin/demo/GroovePerformance",
        container.getPerformance().getName());
    loader.load(new ClassUri(SimplePerformance.class).getUri());
    assertEquals("com/purplepip/odin/demo/SimplePerformance",
        container.getPerformance().getName());
  }
}