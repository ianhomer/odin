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
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Processor responsible for taking MIDI messages off the queue and sending them to the MIDI
 * receivers in a timely manner.  Note that according to the Java MIDI specification messages
 * should not be sent to devices too early.  The time argument in the receiver send method call is
 * really for synchronisation.  If events are fired into a receiver too early then the MIDI
 * instrument might end up handling them early.
 */
@ListenerPriority(9)
@Slf4j
public class DefaultOperationProcessor implements OperationProcessor, PerformanceListener {
  private final PriorityBlockingQueue<OperationEvent> queue;
  private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
  private DefaultOperationProcessorExecutor executor;
  private long refreshPeriod;

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

  @Override
  public void send(Operation operation, long time) throws OdinException {
    OperationEvent operationEvent = new OperationEvent(operation, time);
    queue.add(operationEvent);
  }

  private void start() {
    scheduledPool.scheduleAtFixedRate(executor, 0, refreshPeriod, TimeUnit.MILLISECONDS);
    LOG.debug("Started operation processor");
  }

  private void stop() {
    scheduledPool.shutdown();
    LOG.debug("Closed operation processor");
  }

  @Override
  public void close() {
    stop();
  }

  @Override
  public void onPerformanceStart() {
    start();
  }

  @Override
  public void onPerformanceStop() {
    stop();
  }
}
