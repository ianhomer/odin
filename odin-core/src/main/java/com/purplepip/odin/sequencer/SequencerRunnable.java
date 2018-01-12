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

package com.purplepip.odin.sequencer;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.purplepip.odin.clock.BeatClock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SequencerRunnable implements Runnable {
  private final String name;
  private final BeatClock clock;
  private final MetricRegistry metrics;

  SequencerRunnable(String name, BeatClock clock, MetricRegistry metrics) {
    this.name = name;
    this.clock = clock;
    this.metrics = metrics;
  }

  abstract void doJob();

  @Override
  public void run() {
    if (clock.isStarted()) {
      try {
        doJobWithTiming();
      } catch (RuntimeException e) {
        LOG.error("Exception whilst processing operations", e);
      } catch (Throwable t) {
        /*
         * In an exceptional circumstance where the exception is not handled then log exception
         * and rethrow.
         */
        LOG.error("Uncaught Error whilst processing operations.  " +
            "Note that thread processing might be stopped", t);
        throw t;
      }
    } else {
      if (clock.isStopped()) {
        LOG.debug("Clock has been stopped");
      } else {
        LOG.debug("Clock has not started yet");
      }
    }
  }

  private void doJobWithTiming() {
    final Timer.Context timerContext = metrics.timer(name + ".job").time();
    try {
      doJob();
    } finally {
      timerContext.stop();
    }
  }

  protected BeatClock getClock() {
    return clock;
  }

  protected String getName() {
    return name;
  }

  protected MetricRegistry getMetrics() {
    return metrics;
  }
}
