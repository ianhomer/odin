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

package com.purplepip.odin.store.domain;

/**
 * Test utility to create persistable domain objects in one line.
 */
public final class TestPersistables {
  static PersistablePerformance newPerformance(String name) {
    PersistablePerformance performance = new PersistablePerformance();
    performance.setName(name);
    return performance;
  }

  /**
   * Create new persistable layer.
   *
   * @param name layer name
   * @return new layer
   */
  public static PersistableLayer newLayer(String name) {
    PersistableLayer layer = new PersistableLayer();
    layer.setName(name);
    layer.setLengthNumerator(-1);
    return layer;
  }

  /**
   * Create new persistable layer added to the given performance.
   *
   * @param name layer name
   * @return new layer
   */
  public static PersistableLayer newLayer(PersistablePerformance performance, String name) {
    PersistableLayer layer = newLayer(name);
    performance.addLayer(layer);
    return layer;
  }

}
