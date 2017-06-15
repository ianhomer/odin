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
import com.purplepip.odin.sequence.Clock;
import java.util.concurrent.PriorityBlockingQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultOperationProcessorExecutor implements Runnable {
  private static final int MAX_OPERATIONS_PER_EXECUTION = 1000;
  private static final long FORWARD_POLLING_TIME =
      DefaultOperationProcessor.REFRESH_PERIOD * 1000 * 5;

  private Clock clock;
  private PriorityBlockingQueue<OperationEvent> queue;
  private OperationReceiver operationReceiver;

  DefaultOperationProcessorExecutor(Clock clock,
                                    PriorityBlockingQueue<OperationEvent> queue,
                                    OperationReceiver operationReceiver) {
    this.clock = clock;
    this.queue = queue;
    this.operationReceiver = operationReceiver;
  }

  @Override
  public void run() {
    try {
      doJob();
    } catch (RuntimeException e) {
      LOG.error("Error whilst executing sequence processing", e);
    }
  }

  private void doJob() {
    OperationEvent nextEvent = queue.peek();
    long microsecondPosition = clock.getMicrosecondPosition();
    long count = 0;
    long size = queue.size();
    while (count < MAX_OPERATIONS_PER_EXECUTION && nextEvent != null && nextEvent.getTime()
        < microsecondPosition + FORWARD_POLLING_TIME) {
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
