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

package com.purplepip.odin.properties.beany;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * A Resetter allows all properties that have been set to be reset.
 */
@Slf4j
public class Resetter extends Setter {
  private final Map<String, Object> properties = new HashMap<>();

  /**
   * Reset the properties that have been set onto the new provider AND bind this new provider
   * into this setter.
   *
   * @param provider provider to reset
   */
  public void reset(MutablePropertiesProvider provider) {
    setProvider(provider);
    for (Map.Entry<String, Object> entry : properties.entrySet()) {
      super.set(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public Setter set(String name, Object value) {
    if (!hasDestination()) {
      LOG.warn("Resetter does not have a destination, ignoring setting {} to {}", name, value);
      return this;
    }
    super.set(name, value);
    properties.put(name, value);
    return this;
  }
}
