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

import static org.junit.Assert.assertEquals;

import com.codahale.metrics.Timer;
import com.purplepip.flaky.FlakyTest;
import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.operation.OperationHandler;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SequencerRestartTest {
  private static final int RESTART_COUNT = 50;

  @FlakyTest(3)
  void testRestart() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(100);
    OperationHandler operationReceiver = (operation, time) -> latch.countDown();

    TestSequencerEnvironment environment =
        new TestSequencerEnvironment(operationReceiver, new GroovePerformance());

    try (LogCaptor captor = new LogCapture().warn().withPassThrough().start()) {
      for (int i = 0; i < RESTART_COUNT; i++) {
        environment.start();
        Thread.sleep(10);
        environment.shutdown();
      }
      assertEquals("There should be no warn messages when sequence restarts : " + captor,
          0, captor.size());
    }

    Timer timer = environment.getConfiguration().getMetrics().timer("clock.start");
    assertEquals(RESTART_COUNT, timer.getCount());
  }
}
