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

package com.purplepip.odin.demo;

import com.purplepip.odin.common.Pretty;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.snapshot.Snapshot;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnapshotOperationReceiver implements OperationHandler {
  private final CountDownLatch latch;
  private Snapshot snapshot;
  private boolean expectOverflow;

  /**
   * Create new snapshot operation receiver.
   *
   * @param snapshot snapshot to compare with
   * @param expectedOperationCount expected operation count
   */
  SnapshotOperationReceiver(Snapshot snapshot, int expectedOperationCount, boolean expectOverflow) {
    LOG.debug("Creating SnapshotOperationReceiver with expected count {}", expectedOperationCount);
    latch = new CountDownLatch(expectedOperationCount);
    this.snapshot = snapshot;
    this.expectOverflow = expectOverflow;
  }

  @Override
  public void handle(Operation operation, long time) {
    String prettyTime = Pretty.replaceTrailingZeros(time, 4);
    if (latch.getCount() > 0) {
      snapshot.writeLine(time, String.format("%15s %s",
          prettyTime, operation));
      latch.countDown();
      LOG.trace("Received operation {}", operation);
    } else {
      if (!expectOverflow) {
        /*
         * We should be strict about not sending too much.  If test is complete then operations
         * should not be sent any more.
         */
        LOG.warn(
            "{} : Received operation {} {} even though latch count down complete",
            snapshot.getName(),
            prettyTime,
            operation);
      }
    }
  }

  public CountDownLatch getLatch() {
    return latch;
  }
}
