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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.ClockListener;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor responsible for taking MIDI messages off the queue and sending them to the MIDI
 * receivers in a timely manner.  Note that according to the Java MIDI specification messages
 * should not be sent to devices too early.  The time argument in the receiver send method call is
 * really for synchronisation.  If events are fired into a receiver too early then the MIDI
 * instrument might end up handling them early.
 */
public class DefaultOperationProcessor implements OperationProcessor, ClockListener {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultOperationProcessor.class);

  private static final int MAX_OPERATIONS_PER_EXECUTION = 1000;

  private long refreshPeriod = 50;
  private long forwardPollingTime = refreshPeriod * 1000 * 5;
  private PriorityBlockingQueue<OperationEvent> queue = new PriorityBlockingQueue<>(127,
      new OperationEventComparator());
  private Clock clock;
  private OperationReceiver operationReceiver;
  private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
  private DefaultOperationProcessorExecutor executor;

  /**
   * Create an operation processor.
   *
   * @param clock clock
   * @param operationReceiver operation receiver
   */
  DefaultOperationProcessor(Clock clock, OperationReceiver operationReceiver) {
    LOG.debug("Creating operation processor");
    this.clock = clock;
    if (operationReceiver == null) {
      throw new OdinRuntimeException("OperationReceiver must not be null");
    }
    this.operationReceiver = operationReceiver;
    clock.addListener(this);
    executor = new DefaultOperationProcessorExecutor();
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
  public void onClockStart() {
    start();
  }

  @Override
  public void onClockStop() {
    stop();
  }

  class DefaultOperationProcessorExecutor implements Runnable {
    @Override
    public void run() {
      OperationEvent nextEvent = queue.peek();
      long microsecondPosition = clock.getMicrosecondPosition();
      long count = 0;
      long size = queue.size();
      while (count < MAX_OPERATIONS_PER_EXECUTION && nextEvent != null && nextEvent.getTime()
          < microsecondPosition + forwardPollingTime) {
        try {
          operationReceiver.send(nextEvent.getOperation(), nextEvent.getTime());
          count++;
        } catch (OdinException e) {
          LOG.error("Cannot action operation " + nextEvent.getOperation(), e);
        }
        nextEvent = queue.poll();
      }
      LOG.debug("Processed {} of {} operations at {}", count, size, clock);
    }
  }

}
