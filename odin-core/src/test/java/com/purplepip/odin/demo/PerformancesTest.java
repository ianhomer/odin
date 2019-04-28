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

import static com.purplepip.odin.sequencer.DeltaOdinSequencerConfiguration.deltaConfiguration;
import static org.junit.Assert.assertEquals;

import com.purplepip.flaky.FlakyTest;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.sequencer.TestSequencerEnvironment;
import com.purplepip.odin.snapshot.Snapshot;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PerformancesTest {
  @FlakyTest(3)
  void testSimplePerformance() throws InterruptedException {
    testPerformance(newParameter(new SimplePerformance(), 12));
  }

  @FlakyTest(3)
  void testKotlinPerformance() throws InterruptedException {
    testPerformance(newParameter(new KotlinPerformance(), 5).expectOverflow(true));
  }

  @FlakyTest(3)
  void testPlutoPerformance() throws InterruptedException {
    testPerformance(newParameter(new PlutoPerformance(), 2).expectOverflow(true));
  }

  @FlakyTest(3)
  void testMixinPerformance() throws InterruptedException {
    testPerformance(newParameter(new MixinPerformance(), 20).expectOverflow(true));
  }

  @FlakyTest(3)
  void testGroovePerformance() throws InterruptedException {
    testPerformance(newParameter(new GroovePerformance(), 40)
        .expectOverflow(true)
        .testWaitMs(4000)
        .staticBeatsPerMinute(600));
  }

  private void testPerformance(PerformancesTestParameter parameter) throws InterruptedException {
    String testName = parameter.performance().getClass().getSimpleName();
    Snapshot snapshot = new Snapshot(parameter.performance().getClass(), true);
    SnapshotOperationReceiver snapshotReceiver =
        new SnapshotOperationReceiver(snapshot, parameter.expectedOperationCount(),
            parameter.expectOverflow());

    TestSequencerEnvironment environment =
        new TestSequencerEnvironment(
            snapshotReceiver,
            parameter.performance(),
            deltaConfiguration().staticBeatsPerMinute(parameter.staticBeatsPerMinute()));
    long time = System.currentTimeMillis();
    environment.start();
    try {
      snapshotReceiver.getLatch().await(parameter.testWaitMs(), TimeUnit.MILLISECONDS);
    } finally {
      environment.shutdown();
    }

    /*
     * Warn when test is taking more than half of the wait period.  This is an indication
     * that risk of failure due to time out is high.
     */
    long delta = System.currentTimeMillis() - time;
    if (System.currentTimeMillis() - time > parameter.testWaitMs() / 2) {
      LOG.warn("{} : test is running slow : {} > {}", testName, delta,
          parameter.testWaitMs() / 2);
    }

    /*
     * WARN early to give more information to help when snapshot match fails.
     */
    long actualCount = parameter.expectedOperationCount() - snapshotReceiver.getLatch().getCount();
    if (actualCount != parameter.expectedOperationCount()) {
      LOG.warn(
          "{} : only {} operations were recorded, {} were expected",
          testName,
          actualCount,
          parameter.expectedOperationCount());
    }
    snapshot.expectMatch();
    assertEquals(
        testName + " operation count not as expected",
        parameter.expectedOperationCount(), actualCount);
    LOG.debug("{} : performance snapshot AOK", testName);
  }

  private static PerformancesTestParameter newParameter(
      Performance performance, int expectedOperationCount) {
    return new PerformancesTestParameter()
        .performance(performance)
        .expectedOperationCount(expectedOperationCount);
  }

  @Accessors(fluent = true)
  private static class PerformancesTestParameter {
    private static final int DEFAULT_STATIC_BEATS_PER_MINUTE = 6000;
    private static final int DEFAULT_EXPECTED_OPERATION_COUNT = 20;
    private static final long DEFAULT_WAIT_MS = 8000;

    @Getter @Setter private Performance performance;

    @Getter @Setter private int staticBeatsPerMinute = DEFAULT_STATIC_BEATS_PER_MINUTE;

    @Getter @Setter private int expectedOperationCount = DEFAULT_EXPECTED_OPERATION_COUNT;

    @Getter @Setter private long testWaitMs = DEFAULT_WAIT_MS;

    @Getter @Setter private boolean expectOverflow = false;
  }
}
