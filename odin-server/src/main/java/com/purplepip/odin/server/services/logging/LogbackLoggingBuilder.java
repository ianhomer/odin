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

package com.purplepip.odin.server.services.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LogbackLoggingBuilder {
  private Logging logging = new Logging();
  private boolean withNullLevels = false;

  public LogbackLoggingBuilder withNullLevels() {
    withNullLevels = true;
    return this;
  }

  Logging build() {
    return logging;
  }

  public void add(Logger logger) {
    if (withNullLevels || logger.getLevel() != null) {
      LogCategory category = new LogCategory(logger.getName(),
          toString(logger.getLevel()));
      logging.add(category);
    }
  }

  private String toString(Level level) {
    return level == null ? "null" : level.toString();
  }
}
