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

package com.purplepip.odin.specificity;

import com.purplepip.odin.common.OdinRuntimeException;
import java.util.Optional;

/**
 * Convenience accessor for Name annotation.
 */
public class NameValue {
  private final String value;

  public NameValue(ThingConfiguration thingConfiguration) {
    this(thingConfiguration.getClass());
  }

  /**
   * Create a name value for a given thing configuration class.
   *
   * @param clazz thing configuration class
   */
  public NameValue(Class<? extends ThingConfiguration> clazz) {
    value = Optional.ofNullable(clazz.getAnnotation(Name.class))
        .map(Name::value)
        .orElseThrow(() -> new OdinRuntimeException("Annotation not defined on " + clazz));
  }

  public String get() {
    return value;
  }
}
