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

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import java.util.concurrent.TimeUnit;

public class Profile {
  private static final Snapshot SNAPSHOT = new Snapshot();
  private static final MetricRegistry METRICS = new MetricRegistry();

  public static Snapshot getSnapshot() {
    return SNAPSHOT;
  }

  public static MetricRegistry getMetrics() {
    return METRICS;
  }

  public static SnapshotReport getReport() {
    return new SnapshotReport(getSnapshot());
  }

  public static String getReportAsString() {
    return new MetricsReport(getMetrics()).toString();
  }

  /**
   * Report metrics.
   */
  public static void report() {
    final ConsoleReporter reporter = ConsoleReporter.forRegistry(getMetrics())
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    reporter.report();
  }

  /**
   * Reset profiling.
   */
  public static void reset() {
    SNAPSHOT.reset();
    METRICS.getMetrics().forEach((name, metric) -> {
      METRICS.remove(name);
    });
  }
}
