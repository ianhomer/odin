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

package com.purplepip.odin.profile;

import static com.purplepip.odin.sequencer.DeltaOdinSequencerConfiguration.deltaConfiguration;

import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.sequencer.LoggingOperationReceiver;
import com.purplepip.odin.sequencer.TestSequencerEnvironment;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ProfileSequencerTest {

  @Test
  void testProfileSequencer() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1_000);
    OperationHandler operationReceiver = (operation, time) -> latch.countDown();

    spinUp();
    TestSequencerEnvironment environment =
        new TestSequencerEnvironment(
            operationReceiver,
            new GroovePerformance(),
            deltaConfiguration().clockStartOffset(500_000));
    LOG.info("Sequence count : {}", environment.getContainer().getSequenceStream().count());
    for (int i = 1; i < 3; i++) {
      environment.prepare();
      Profile.reset();
      environment.start();
      latch.await(5_000, TimeUnit.MILLISECONDS);
      environment.stop();
      // BeatClock start running at : 5919978micros on local dev machine for
      // first execution... convert this into
      // hardware specific assertion and work to improve and assert lower.
      LOG.info("Execution {} : \n{}", i, Profile.getReportAsString());
      Profile.reset();
    }

    environment.shutdown();
  }

  private void spinUp() {
    for (int i = 0; i < 100; i++) {
      TestSequencerEnvironment environment =
          new TestSequencerEnvironment(new LoggingOperationReceiver(), new GroovePerformance());
      environment.prepare();
      environment.start();
      environment.stop();
    }
  }
}
