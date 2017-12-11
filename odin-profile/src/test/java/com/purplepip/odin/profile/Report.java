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

package com.purplepip.odin.profile;

import java.util.Formatter;
import java.util.Locale;

public class Report {
  private Snapshot snapshot;

  public Report(Snapshot snapshot) {
    this.snapshot = snapshot;
  }

  /**
   * Send report summary to log.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb, Locale.ENGLISH);
    sb.append(snapshot).append('\n');
    snapshot.getSortedStream().forEach(record -> {
      formatter.format("%20d : %s\n", record.getTime(), record.getName());
    });
    return sb.toString();
  }
}
