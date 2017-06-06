package com.purplepip.logcapture;

import ch.qos.logback.classic.Level;

/**
 * Builder for a Log Capture class.
 */
public class LogCapture {
  LogCaptureConfiguration configuration = new LogCaptureConfiguration();

  public LogCapture debug() {
    configuration.setLevel(Level.DEBUG);
    return this;
  }

  public LogCapture from(String category) {
    configuration.setCategory(category);
    return this;
  }

  public LogCapture from(Class clazz) {
    configuration.setCategory(clazz.getName());
    return this;
  }

  public LogCaptor start() {
    return new LogCaptor(configuration);
  }
}
