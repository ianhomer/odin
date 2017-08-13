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

/**
 * Log capture configuration.
 */
public class LogCaptureConfiguration {
  private Level level = Level.INFO;
  private String category = "ROOT";
  private boolean passThrough = false;

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

  public void setPassThrough(boolean passThrough) {
    this.passThrough = passThrough;
  }

  public boolean getPassThrough() {
    return passThrough;
  }
}
