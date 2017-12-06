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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.common.Pretty;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.snapshot.Snapshot;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnapshotOperationReceiver implements OperationReceiver {
  private final CountDownLatch latch;
  private Snapshot snapshot;

  /**
   * Create new snapshot operation receiver.
   *
   * @param snapshot snapshot to compare with
   * @param expectedOperationCount expected operation count
   */
  SnapshotOperationReceiver(Snapshot snapshot, int expectedOperationCount) {
    LOG.debug("Creating SnapshotOperationReceiver with expected count {}", expectedOperationCount);
    latch = new CountDownLatch(expectedOperationCount);
    this.snapshot = snapshot;
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    if (latch.getCount() > 0) {
      latch.countDown();
      snapshot.writeLine(String.format("%15s %s",
          Pretty.replaceTrailingZeros(time, 4), operation));
      LOG.trace("Received operation {}", operation);
    } else {
      /*
       * We should be strict about not sending too much.  If test is complete then operations
       * should not be sent any more.
       */
      LOG.warn("Received operation {} even though latch count down complete", operation);
    }
  }

  public CountDownLatch getLatch() {
    return latch;
  }
}
