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

import com.purplepip.odin.bag.Thing;
import com.purplepip.odin.common.OdinRuntimeException;

/**
 * Specifics utility class.
 */
public final class Specifics {
  private Specifics() {
  }

  /**
   * Get name for the given class.
   *
   * @param clazz class to inspect
   * @return flow name
   */
  public static String getName(Class<? extends Thing> clazz) {
    if (clazz.isAnnotationPresent(Name.class)) {
      Name definition = clazz.getAnnotation(Name.class);
      return definition.value();
    } else {
      throw new OdinRuntimeException("Cannot find flow name for " + clazz);
    }
  }
}
