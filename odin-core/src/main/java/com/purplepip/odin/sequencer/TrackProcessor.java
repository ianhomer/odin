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
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.composition.ListenerPriority;
import com.purplepip.odin.composition.clock.BeatClock;
import com.purplepip.odin.composition.clock.ClockListener;
import com.purplepip.odin.composition.track.Track;
import com.purplepip.odin.sequencer.statistics.MutableSequenceProcessorStatistics;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Track processor.
 */
@Slf4j
@ListenerPriority(9)
public class TrackProcessor implements ClockListener {
  /*
   * Time in milliseconds between each processor execution.
   */
  private long refreshPeriod = 200;
  private ScheduledExecutorService scheduledPool;
  private TrackProcessorExecutor executor;
  private boolean running;

  /**
   * Create a series processor.
   *
   * @param clock clock
   * @param tracks tracks
   * @param operationProcessor operation processor
   */
  TrackProcessor(BeatClock clock,
                 Things<Track> tracks,
                 OperationProcessor operationProcessor,
                 MutableSequenceProcessorStatistics statistics,
                  MetricRegistry metrics) {
    scheduledPool = Executors.newScheduledThreadPool(1);
    executor = new TrackProcessorExecutor(
        clock, tracks, operationProcessor, refreshPeriod,
        statistics, metrics
    );
    clock.addListener(this);
    LOG.debug("Created sequence processor");
  }

  @Override
  public void onClockStart() {
    start();
  }


  @Override
  public void onClockStop() {
    stop();
  }

  private void start() {
    scheduledPool.scheduleAtFixedRate(executor, 0, refreshPeriod, TimeUnit.MILLISECONDS);
    running = true;
    LOG.debug("Started sequence processor");
  }

  private void stop() {
    scheduledPool.shutdown();
    LOG.debug("Stopped sequence processor");
  }

  public void processOnce() {
    executor.run();
  }

  public boolean isRunning() {
    return running;
  }

  /**
   * Close sequence processor.
   */
  public void close() {
    stop();
  }
}
