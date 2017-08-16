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
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggingController {
  @RequestMapping("/services/logging")
  public Logging getLogging() {
    return getLogbackLogging();
  }

  @RequestMapping("/services/logging/{name}/{level}")
  public Logging setLogging(@PathVariable("name") String name,
                            @PathVariable("level") String level) {
    Logger logger = getLoggerContext().getLogger(level);
    logger.setLevel(Level.toLevel(level));
    return getLogbackLogging();
  }

  private LoggerContext getLoggerContext() {
    return  (LoggerContext) LoggerFactory.getILoggerFactory();
  }

  private Logging getLogbackLogging() {
    LogbackLoggingBuilder builder = new LogbackLoggingBuilder();
    LoggerContext context = getLoggerContext();
    context.getLoggerList().forEach(logger -> builder.add(logger));
    return builder.build();
  }


}
