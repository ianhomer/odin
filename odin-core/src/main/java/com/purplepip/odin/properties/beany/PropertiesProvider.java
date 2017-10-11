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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.stream.Stream;

public interface PropertiesProvider {
  /**
   * Get sequence property value.
   */
  String getProperty(String name);

  /**
   * Names of properties set for this sequence.
   *
   * @return names of properties
   */
  @JsonIgnore
  Stream<String> getPropertyNames();

  /**
   * Are properties explicitly declared.  This should be true if all the properties set
   * are declared explicitly as getters (and setters in mutable object).  This allows
   * configuration management (e.g. persistence) to work with generic objects which then
   * get mapped to strongly typed objects in the runtime.
   *
   * @return are properties explicitly declared
   */
  @JsonIgnore
  default boolean arePropertiesDeclared() {
    return false;
  }
}
