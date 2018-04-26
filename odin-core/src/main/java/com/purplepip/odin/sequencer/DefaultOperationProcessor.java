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
import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.PerformanceListener;
import com.purplepip.odin.common.ListenerPriority;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.operation.OperationReceiver;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Processor responsible for taking MIDI messages off the queue and sending them to the MIDI
 * receivers in a timely manner.  Note that according to the Java MIDI specification messages
 * should not be sent to devices too early.  The time argument in the receiver handle method call is
 * really for synchronisation.  If events are fired into a receiver too early then the MIDI
 * instrument might end up handling them early.
 */
@ListenerPriority(9)
@Slf4j
public class DefaultOperationProcessor implements OperationProcessor, PerformanceListener {
  private final PriorityBlockingQueue<OperationEvent> queue;
  private ScheduledExecutorService scheduledPool;
  private DefaultOperationProcessorExecutor executor;
  private final long refreshPeriod;

  /**
   * Create an operation processor.
   *
   * @param clock clock
   * @param operationReceiver operation receiver
   */
  DefaultOperationProcessor(BeatClock clock, OperationReceiver operationReceiver,
                            MetricRegistry metrics, long refreshPeriod,
                            boolean strictOrder) {
    this.refreshPeriod = refreshPeriod;

    Comparator<OperationEvent> comparator = strictOrder
        ? new StrictOperationEventComparator() : new OperationEventComparator();
    queue = new PriorityBlockingQueue<>(127, comparator);
    if (operationReceiver == null) {
      throw new OdinRuntimeException("OperationReceiver must not be null");
    }
    clock.addListener(this);
    executor = new DefaultOperationProcessorExecutor(clock, queue, operationReceiver, metrics,
        refreshPeriod);
    LOG.debug("Created operation processor");
  }

  void runOnce() {
    LOG.debug("Running operation processor once");
    executor.run();
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    OperationEvent operationEvent = new OperationEvent(operation, time);
    queue.add(operationEvent);
  }

  @Override
  public void onPerformancePrepare() {
    scheduledPool = Executors.newScheduledThreadPool(1);
    scheduledPool.scheduleAtFixedRate(executor, 0, refreshPeriod, TimeUnit.MILLISECONDS);
    LOG.debug("Prepared operation processor");
  }

  @Override
  public void onPerformanceShutdown() {
    shutdown();
  }

  private void shutdown() {
    if (scheduledPool != null) {
      scheduledPool.shutdown();
      try {
        scheduledPool.awaitTermination(refreshPeriod * 2, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        LOG.error("Could not shut operation processor down cleanly", e);
        Thread.currentThread().interrupt();
      }
    }
    if (!queue.isEmpty()) {
      LOG.warn("Operation processor queue is not empty, size = {}, executing operator processor",
          queue.size());
      executor.run();
      if (!queue.isEmpty()) {
        LOG.warn("Operation processor queue is still not empty, size = {}, "
            + " after operator processor execution", queue.size());
      }
    }
    LOG.debug("Closed operation processor");
  }

  @Override
  public void close() {
    shutdown();
  }

}
