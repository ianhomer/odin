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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.ClockListener;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SequenceRuntime processor.
 */
public class SequenceProcessor implements ClockListener {
  private final Set<SequenceTrack> sequenceTrackSet;
  private final Clock clock;
  private final OperationProcessor operationProcessor;
  private long refreshPeriod = 200;
  private ScheduledExecutorService scheduledPool;

  /**
   * Create a series processor.
   *
   * @param clock clock
   * @param sequenceTrackSet series track set
   * @param operationProcessor operation processor
   */
  SequenceProcessor(Clock clock,
                    Set<SequenceTrack> sequenceTrackSet,
                    OperationProcessor operationProcessor) {
    this.sequenceTrackSet = Collections.unmodifiableSet(sequenceTrackSet);
    this.clock = clock;
    this.operationProcessor = operationProcessor;
    clock.addListener(this);
    /*
     * If clock has already started then start immediately, otherwise wait for clock start
     * event.
     */
    if (clock.isStarted()) {
      start();
    }
  }

  @Override
  public void onClockStart() {
    start();
  }


  @Override
  public void onClockStop() {
    scheduledPool.shutdown();
  }

  private void start() {
    scheduledPool = Executors.newScheduledThreadPool(1);
    SequenceProcessorExecutor executor = new SequenceProcessorExecutor(
        clock, sequenceTrackSet, operationProcessor, refreshPeriod
    );
    scheduledPool.scheduleWithFixedDelay(executor, 0, refreshPeriod, TimeUnit.MILLISECONDS);
  }

  /**
   * Close sequence processor.
   */
  public void close() {
    onClockStop();
  }
}
