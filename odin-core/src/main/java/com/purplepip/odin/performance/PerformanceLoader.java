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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.operation.OperationHandler;
import java.net.URI;

public interface PerformanceLoader extends OperationHandler {
  void load(URI getPerformanceUri);

  /**
   * Load performance specified in load performance operation.
   *
   * @param operation load performance operation
   */
  default void handle(Operation operation, long time) {
    if (operation instanceof LoadPerformanceOperation) {
      URI performanceUri = ((LoadPerformanceOperation) operation).getPerformanceUri();
      if (canLoad(performanceUri)) {
        load(performanceUri);
      } else {
        throw new OdinRuntimeException("Loader " + this + " does not know how to load performance "
            + performanceUri);
      }
    }
  }

  boolean canLoad(URI getPerformanceUri);
}
