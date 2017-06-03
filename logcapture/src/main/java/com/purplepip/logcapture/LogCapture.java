package com.purplepip.logcapture;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

/**
 * Log capture tool.
 */
public class LogCapture implements AutoCloseable {
  public static LogCapture capture(String category) {
    return new LogCapture(category);
  }

  private ListAppender capturingAppender = new ListAppender();
  private String category;
  private LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

  private List<Appender<ILoggingEvent>> removedAppenders = new ArrayList<>();

  private LogCapture(String category) {
    this.category = category;
    Logger logger = loggerContext.getLogger(category);

    for (Appender<ILoggingEvent> appenderToRemove :
        ImmutableList.copyOf(logger.iteratorForAppenders())) {
      logger.detachAppender(appenderToRemove);
      removedAppenders.add(appenderToRemove);
    }

    capturingAppender.setContext(loggerContext);
    logger.addAppender(capturingAppender);
    capturingAppender.start();
  }

  @Override
  public void close() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger logger = loggerContext.getLogger(category);

    capturingAppender.stop();
    logger.detachAppender(capturingAppender);

    for (Appender<ILoggingEvent> appender : removedAppenders) {
      logger.addAppender(appender);
    }
  }

  public int size() {
    return capturingAppender.list.size();
  }
}
