package com.purplepip.logcapture;

import ch.qos.logback.classic.Level;

/**
 * Log capture configuration.
 */
public class LogCaptureConfiguration {
  private Level level = Level.INFO;
  private String category = "ROOT";

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
