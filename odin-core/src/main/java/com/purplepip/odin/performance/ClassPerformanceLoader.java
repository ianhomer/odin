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

import com.purplepip.odin.common.ClassUri;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassPerformanceLoader extends AbstractPerformanceLoader {
  private final PerformanceContainer container;
  private final Map<URI, Performance> performanceMap;

  public ClassPerformanceLoader(Performance performance) {
    this(Collections.singletonList(performance));
  }

  public ClassPerformanceLoader(List<Performance> performances) {
    this(performances, new DefaultPerformanceContainer());
  }

  public ClassPerformanceLoader(Performance performance, PerformanceContainer container) {
    this(Collections.singletonList(performance), container);
  }

  public ClassPerformanceLoader(List<Performance> performances, PerformanceContainer container) {
    this.container = container;
    this.performanceMap = toPerformanceMap(performances);
  }

  /**
   * Cerate a class performance loader.
   *
   * @param performances performances to load from
   * @param container performance container
   * @param overlay overlay
   */
  public ClassPerformanceLoader(List<Performance> performances, PerformanceContainer container,
                                Performance overlay) {
    super(overlay);
    this.container = container;
    this.performanceMap = toPerformanceMap(performances);
  }

  private Map<URI, Performance> toPerformanceMap(List<Performance> performances) {
    return performances.stream().collect(
        Collectors.toMap(p -> new ClassUri(p.getClass(), true).getUri(), p -> p));
  }

  @Override
  public void load(URI performanceUri) {
    if (!performanceMap.containsKey(performanceUri)) {
      LOG.error("Cannot load performance " + performanceUri + ", has it been registered in "
          + performanceMap);
    } else {
      container.setPerformance(overlay(performanceMap.get(performanceUri)));
      container.apply();
      LOG.info("Loaded performance {}", performanceUri);
    }
  }

  @Override
  public boolean canLoad(URI performanceUri) {
    return performanceUri.getScheme() == null || "classpath".equals(performanceUri.getScheme());
  }
}
