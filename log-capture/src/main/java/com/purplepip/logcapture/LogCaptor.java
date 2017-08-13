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

package com.purplepip.logcapture;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 * Log capture tool.
 */
public class LogCaptor implements AutoCloseable {
  private LogCaptureConfiguration configuration;
  private Level originalLevel;
  private ListAppender<ILoggingEvent> capturingAppender = new ListAppender<>();
  private LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
  private Map<String, Appender<ILoggingEvent>> removedAppenders = new HashMap<>();

  LogCaptor(LogCaptureConfiguration configuration) {
    this.configuration = configuration;
    start();
  }

  private void start() {
    if (!configuration.getPassThrough()) {
      /*
       * Remove all appenders.
       */
      for (Logger logger : context.getLoggerList()) {
        for (Appender<ILoggingEvent> appender :
            ImmutableList.copyOf(logger.iteratorForAppenders())) {
          logger.detachAppender(appender);
          removedAppenders.put(logger.getName(), appender);
        }
      }
    }

    /*
     * Set up the capturing appender.
     */
    Logger logger = context.getLogger(configuration.getCategory());
    capturingAppender.setContext(context);
    logger.addAppender(capturingAppender);
    originalLevel = logger.getLevel();
    logger.setLevel(configuration.getLevel());
    capturingAppender.start();
  }

  @Override
  public void close() {
    detachCapturingAppender();

    if (!configuration.getPassThrough()) {
      /*
       * Restore previous appenders.
       */
      for (Map.Entry<String, Appender<ILoggingEvent>> entry : removedAppenders.entrySet()) {
        Logger logger = context.getLogger(entry.getKey());
        logger.addAppender(entry.getValue());
      }
    }
  }

  /*
   * Remove capturing appender.
   */
  private void detachCapturingAppender() {
    Logger logger = context.getLogger(configuration.getCategory());
    capturingAppender.stop();
    logger.detachAppender(capturingAppender);
    logger.setLevel(originalLevel);
  }

  public int size() {
    return capturingAppender.list.size();
  }

  public String getMessage(int index) {
    return capturingAppender.list.get(index).getFormattedMessage();
  }

  public boolean hasMessages() {
    return size() > 0;
  }
}
