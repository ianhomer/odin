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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.sequencer.TestSequencerEnvironment;
import com.purplepip.odin.snapshot.Snapshot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Slf4j
@RunWith(Parameterized.class)
public class PerformancesTest {
  private Performance performance;
  private long testWait;
  private int staticBeatsPerMinute;
  private int expectedOperationCount;

  /**
   * Create performances test from injected parameter.
   *
   * @param parameter injected parameter
   */
  public PerformancesTest(PerformanceTestParameter parameter) {
    performance = parameter.performance();
    staticBeatsPerMinute = parameter.staticBeatsPerMinute();
    expectedOperationCount = parameter.expectedOperationCount();
    testWait = parameter.testWait();
  }

  /**
   * Get parameters for parameterized tests.
   *
   * @return parameters
   */
  @Parameterized.Parameters
  public static Iterable<PerformanceTestParameter> parameters() {
    Collection<PerformanceTestParameter> parameters = new ArrayList<>();
    parameters.add(newParameter(new SimplePerformance(), 12));
    parameters.add(newParameter(new GroovePerformance(), 50)
        .staticBeatsPerMinute(600));
    return parameters;
  }

  @Test
  public void testPerformance() throws OdinException, InterruptedException {
    Snapshot snapshot = new Snapshot(performance.getClass());
    SnapshotOperationReceiver snapshotReceiver =
        new SnapshotOperationReceiver(snapshot, expectedOperationCount);

    TestSequencerEnvironment environment =
        new TestSequencerEnvironment(snapshotReceiver, performance,
            deltaConfiguration().staticBeatsPerMinute(staticBeatsPerMinute));
    long time = System.currentTimeMillis();
    environment.start();
    try {
      snapshotReceiver.getLatch().await(testWait, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    /*
     * Warn when test is taking more than half of the wait period.  This is an indication
     * that risk of failure due to time out is high.
     */
    long delta = System.currentTimeMillis() - time;
    if (System.currentTimeMillis() - time > testWait / 2) {
      LOG.warn("Test is running slow : {} > {}", delta, testWait / 2);
    }

    /*
     * WARN early to give more information to help when snapshot match fails.
     */
    long actualCount = snapshotReceiver.getLatch().getCount();
    if (actualCount != 0) {
      LOG.warn("Only {} operations were recorded, {} were expected", actualCount,
          expectedOperationCount);
    }
    snapshot.expectMatch();
    assertEquals(0, actualCount);
    LOG.debug("Performance snapshot AOK");
  }

  static PerformanceTestParameter newParameter(Performance performance,
                                               int expectedOperationCount) {
    return new PerformanceTestParameter().performance(performance)
        .expectedOperationCount(expectedOperationCount);
  }

  @Accessors(fluent = true)
  private static class PerformanceTestParameter {
    private static final int DEFAULT_STATIC_BEATS_PER_MINUTE = 6000;
    private static final int DEFAULT_EXPECTED_OPERATION_COUNT = 20;
    private static final long DEFAULT_WAIT = 2000;

    @Getter
    @Setter
    private Performance performance;

    @Getter @Setter
    private int staticBeatsPerMinute = DEFAULT_STATIC_BEATS_PER_MINUTE;

    @Getter @Setter
    private int expectedOperationCount = DEFAULT_EXPECTED_OPERATION_COUNT;

    @Getter @Setter
    private long testWait = DEFAULT_WAIT;
  }
}
