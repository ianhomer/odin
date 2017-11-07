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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.clock.BeatClock;
import java.util.concurrent.PriorityBlockingQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultOperationProcessorExecutor implements Runnable {
  private static final int MAX_OPERATIONS_PER_EXECUTION = 1000;
  private static final long FORWARD_POLLING_TIME =
      DefaultOperationProcessor.REFRESH_PERIOD * 1000 * 5;

  private final BeatClock clock;
  private final PriorityBlockingQueue<OperationEvent> queue;
  private final OperationReceiver operationReceiver;
  private final Timer jobMetric;
  private final Meter sentMetric;
  private final Meter failureMetric;

  DefaultOperationProcessorExecutor(BeatClock clock,
                                    PriorityBlockingQueue<OperationEvent> queue,
                                    OperationReceiver operationReceiver,
                                    MetricRegistry metrics) {
    this.clock = clock;
    this.queue = queue;
    jobMetric = metrics.timer("operation.job");
    sentMetric = metrics.meter("operation.sent");
    failureMetric = metrics.meter("operation.failure");
    this.operationReceiver = operationReceiver;
  }

  @Override
  public void run() {
    final Timer.Context timerContext = jobMetric.time();
    try {
      doJob();
    } catch (RuntimeException e) {
      LOG.error("Error whilst executing sequence processing", e);
    } finally {
      timerContext.stop();
    }
  }

  private void doJob() {
    long size = queue.size();
    long microsecondPosition = clock.getMicroseconds();
    long count = 0;
    /*
     * Peek at next event on queue first to see whether we are ready to process this operation.
     */
    OperationEvent nextEvent = queue.peek();
    while (count < MAX_OPERATIONS_PER_EXECUTION && nextEvent != null && nextEvent.getTime()
        < microsecondPosition + FORWARD_POLLING_TIME) {
      /*
       * If we are ready for next event then take it off the queue and process.
       */
      nextEvent = queue.poll();
      try {
        operationReceiver.send(nextEvent.getOperation(), nextEvent.getTime());
        sentMetric.mark();
        count++;
      } catch (OdinException e) {
        failureMetric.mark();
        LOG.error("Cannot action operation " + nextEvent.getOperation(), e);
      }
      nextEvent = queue.peek();
    }
    LOG.debug("Processed {} of {} operations at {}", count, size, clock);
  }
}
