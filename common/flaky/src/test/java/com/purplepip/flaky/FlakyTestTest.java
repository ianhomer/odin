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

package com.purplepip.flaky;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import static org.junit.platform.testkit.ExecutionEventConditions.container;
import static org.junit.platform.testkit.ExecutionEventConditions.displayName;
import static org.junit.platform.testkit.ExecutionEventConditions.event;
import static org.junit.platform.testkit.ExecutionEventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.ExecutionEventConditions.finishedWithFailure;
import static org.junit.platform.testkit.TestExecutionResultConditions.message;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.testkit.ExecutionRecorder;
import org.junit.platform.testkit.ExecutionsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See RepeatedTestTests in junit source for example on testing junit extensions.
 */
class FlakyTestTest {
  @Test
  void neverFailsSuccessful() {
    ExecutionsResult executionsResult = execute(selectMethod(TestCase.class, "neverFails"));
    assertThat(executionsResult.getExecutionEvents())
        .haveExactly(1,
            event(container(), displayName("neverFails()"),
            finishedSuccessfully()));
  }

  @Test
  void alwaysFailsFails() {
    ExecutionsResult executionsResult = execute(selectMethod(TestCase.class, "alwaysFails"));
    assertThat(executionsResult.getExecutionEvents())
        .haveExactly(1,
            event(finishedWithFailure(message(value -> value.contains("Flaked out")))));
  }

  @Test
  void succeedsEveryFourTimesSucceeds() {
    ExecutionsResult executionsResult = execute(selectMethod(TestCase.class,
        "succeedsEveryFourTimesSucceeds"));
    assertThat(executionsResult.getExecutionEvents())
        .haveExactly(1, finishedSuccessfully());
  }


  private ExecutionsResult execute(DiscoverySelector... selectors) {
    return ExecutionRecorder.execute(new JupiterTestEngine(),
        request().selectors(selectors).build());
  }

  static class TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(FlakyTestTest.TestCase.class);
    private static final AtomicInteger mostTimeCount = new AtomicInteger(1);

    @FlakyTest
    void neverFails() {
    }

    @FlakyTest(5)
    void succeedsEveryFourTimes() {
      LOG.info("mostTimeCount = {}", mostTimeCount);
      if (mostTimeCount.incrementAndGet() % 8 != 0) {
        fail("succeedsEveryFourTimes failed");
      }
    }

    @FlakyTest
    void alwaysFails() {
      fail("alwaysFails failed");
    }

  }

}
