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
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public abstract class AbstractPerformanceTest {
  private static final int DEFAULT_STATIC_BEATS_PER_MINUTE = 6000;
  private static final int DEFAULT_EXPECTED_OPERATION_COUNT = 20;
  private static final long DEFAULT_WAIT = 2000;

  private int staticBeatsPerMinute = DEFAULT_STATIC_BEATS_PER_MINUTE;
  private int expectedOperationCount = DEFAULT_EXPECTED_OPERATION_COUNT;
  private Performance performance;

  protected abstract Performance newPerformance();

  protected void setStaticBeatsPerMinute(int staticBeatsPerMinute) {
    this.staticBeatsPerMinute = staticBeatsPerMinute;
  }

  protected void setExpectedOperationCount(int expectedOperationCount) {
    this.expectedOperationCount = expectedOperationCount;
  }

  protected Performance getPerformance() {
    return performance;
  }

  @Before
  public void baseSetUp() {
    performance = newPerformance();
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
      snapshotReceiver.getLatch().await(DEFAULT_WAIT, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    /*
     * Warn when test is taking more than half of the wait period.  This is an indication
     * that risk of failure due to time out is high.
     */
    long delta = System.currentTimeMillis() - time;
    if (System.currentTimeMillis() - time > DEFAULT_WAIT / 2) {
      LOG.warn("Test is running slow : {} > {}", delta, DEFAULT_WAIT / 2);
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
}
