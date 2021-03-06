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

package com.purplepip.odin.creation.action;

import com.purplepip.odin.properties.Properties;

/**
 * Sequences utility methods.
 */
public final class Actions {
  private Actions() {
  }

  /**
   * Copy core values from one action to another.
   *
   * @param from where to copy values from
   * @param to where to copy values to
   */
  public static void copyCoreValues(ActionConfiguration from, MutableActionConfiguration to) {
    to.setName(from.getName());
    Properties.copyProperties(from, to);
  }
}
