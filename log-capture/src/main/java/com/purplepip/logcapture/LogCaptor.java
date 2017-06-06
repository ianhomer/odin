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

  LogCaptor(LogCaptureConfiguration configuration) {
    this.configuration = configuration;
    start();
  }

  private ListAppender<ILoggingEvent> capturingAppender = new ListAppender<>();
  private LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

  private Map<String, Appender<ILoggingEvent>> removedAppenders = new HashMap<>();

  private void start() {
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

    /*
     * Restore previous appenders.
     */
    for (Map.Entry<String, Appender<ILoggingEvent>> entry : removedAppenders.entrySet()) {
      Logger logger = context.getLogger(entry.getKey());
      logger.addAppender(entry.getValue());
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
}
