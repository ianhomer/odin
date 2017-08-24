/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.common.OdinRuntimeException;

/**
 * Flows utility class.
 */
public final class Flows {
  private Flows() {
  }

  /**
   * Get flow name for the given flow class.
   *
   * @param flowClass flow class to inspect
   * @return flow name
   */
  public static String getFlowName(Class<? extends Flow> flowClass) {
    if (flowClass.isAnnotationPresent(FlowDefinition.class)) {
      FlowDefinition definition = flowClass.getAnnotation(FlowDefinition.class);
      return definition.name();
    } else {
      throw new OdinRuntimeException("Cannot find flow name for " + flowClass);
    }
  }
}
