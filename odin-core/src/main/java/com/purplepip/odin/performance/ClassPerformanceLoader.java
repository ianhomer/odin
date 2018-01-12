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

import java.net.URI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassPerformanceLoader extends AbstractPerformanceLoader {
  private final PerformanceContainer container;

  public ClassPerformanceLoader(PerformanceContainer container) {
    this.container = container;
  }

  public ClassPerformanceLoader(PerformanceContainer container, Performance overlay) {
    super(overlay);
    this.container = container;
  }

  @Override
  public void load(URI performanceUri) {
    try {
      String schemeSpecificPart = performanceUri.getSchemeSpecificPart();
      String className = schemeSpecificPart.replace('/', '.');
      container.setPerformance(overlay((Performance)
          getClass().getClassLoader().loadClass(className).newInstance()));
      container.apply();
      LOG.info("Loaded performance {}", performanceUri);
    } catch (NoClassDefFoundError | ClassNotFoundException | IllegalAccessException
        | InstantiationException e) {
      LOG.error("Cannot load performance " + performanceUri,e);
    }
  }

  @Override
  public boolean canLoad(URI performanceUri) {
    return "classpath".equals(performanceUri.getScheme());
  }
}
