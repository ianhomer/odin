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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class MetricsReport {
  private static final String TIMER_OVERHEAD = "timer overhead";
  private MetricRegistry registry;
  private double timerOverhead;

  public MetricsReport(MetricRegistry registry) {
    this.registry = registry;
  }

  /**
   * Send report summary to log.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb, Locale.ENGLISH);

    timerOverhead = registry.timer(TIMER_OVERHEAD).getSnapshot().getMean();

    registry.getTimers().entrySet().stream()
        .filter(entry -> !entry.getKey().equals(TIMER_OVERHEAD))
        .sorted(Comparator.comparing(entry ->
            -calculateTotal(entry.getValue())))
        .forEach(entry -> append(formatter, entry));

    registry.getTimers().entrySet().stream()
        .filter(entry -> entry.getKey().equals(TIMER_OVERHEAD))
        .forEach(entry -> append(formatter, entry));

    return sb.toString();
  }

  private double calculateTotal(Timer timer) {
    return (timer.getSnapshot().getMean() - timerOverhead) * timer.getCount();
  }

  private void append(Formatter formatter, Map.Entry<String, Timer> entry) {
    Snapshot snapshot = entry.getValue().getSnapshot();

    formatter.format("%20.0f : %5d : %10.0f : %s\n",
        snapshot.getMean(), entry.getValue().getCount(),
        calculateTotal(entry.getValue()),
        toPretty(entry.getKey()));
  }

  private String toPretty(String name) {
    return name
        .replaceFirst("execution\\(", "")
        .replaceFirst("\\)$", "");
  }
}
