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

package com.purplepip.odin.bag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;

/**
 * A generic identifiable thing.
 */
public interface Thing {
  /**
   * Get ID for the thing.
   *
   * @return id
   */
  @JsonIgnore
  long getId();

  /**
   * Get the name for the thing.
   *
   * @return name
   */
  @NotNull
  String getName();

  /**
   * Initialise transient properties before use.  Initialise should be executable at any time
   * and the system should be able to restore a valid state.  Note that initialise does not
   * always need to restore the same state since their might be random elements or external inputs.
   * Furthermore initialise might be relatively expensive to run so the system will only
   * call initialise just before it is needed.
   */
  default void initialise() {}
}
