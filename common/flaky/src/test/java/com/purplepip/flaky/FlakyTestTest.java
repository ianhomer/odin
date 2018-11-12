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

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import static org.junit.platform.testkit.engine.EventConditions.container;
import static org.junit.platform.testkit.engine.EventConditions.displayName;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.engine.EventConditions.finishedWithFailure;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

/**
 * See RepeatedTestTests in junit source for example on testing junit extensions.
 */
class FlakyTestTest {
  @Test
  void neverFailsSuccessful() {
    EngineExecutionResults executionsResult = execute(selectMethod(TestCase.class, "neverFails"));
    executionsResult.all().assertThatEvents()
        .haveExactly(1,
            event(container(), displayName("neverFails()"),
            finishedSuccessfully()));
  }

  @Test
  void alwaysFailsFails() {
    EngineExecutionResults executionsResult = execute(selectMethod(TestCase.class, "alwaysFails"));
    executionsResult.all().assertThatEvents()
        .haveExactly(1,
            event(finishedWithFailure(message(value -> value.contains("Flaked out")))));
  }

  @Test
  void succeedsEveryFourTimesSucceeds() {
    EngineExecutionResults executionsResult = execute(selectMethod(TestCase.class,
        "succeedsEveryFourTimes"));
    executionsResult.all().assertThatEvents()
        .haveExactly(1,
            event(container(), displayName("succeedsEveryFourTimes()"),
                finishedSuccessfully()));
  }


  private EngineExecutionResults execute(DiscoverySelector... selectors) {
    return EngineTestKit.execute(new JupiterTestEngine(),
        request().selectors(selectors).build());
  }

  static class TestCase {
    private static final AtomicInteger succeedsEveryFourTimesCount = new AtomicInteger(1);

    @FlakyTest
    void neverFails() {
    }

    @FlakyTest(3)
    void alwaysFails() {
      fail("alwaysFails failed");
    }

    @FlakyTest(5)
    void succeedsEveryFourTimes() {
      if (succeedsEveryFourTimesCount.incrementAndGet() % 4 != 0) {
        fail("succeedsEveryFourTimes failed");
      }
    }
  }
}
