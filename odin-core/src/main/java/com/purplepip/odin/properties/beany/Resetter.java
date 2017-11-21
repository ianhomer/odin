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

/**
 * A Resetter allows all properties that have been set to be reset.
 */
public class Resetter extends Setter {
  Map<String, Object> properties = new HashMap<>();

  public Resetter(MutablePropertiesProvider provider) {
    super(provider);
  }

  public void reset(MutablePropertiesProvider provider) {
    setProvider(provider);
    for (Map.Entry<String, Object> entry : properties.entrySet()) {
      super.set(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public Setter set(String name, Object value) {
    super.set(name, value);
    properties.put(name, value);
    return this;
  }
}
