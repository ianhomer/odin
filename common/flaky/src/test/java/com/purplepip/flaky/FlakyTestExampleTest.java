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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example test for manual testing of FlakyTest annotation.
 */
class FlakyTestExampleTest {
  private static final AtomicInteger succeedsEveryFourTimesCount = new AtomicInteger(0);
  private static final AtomicInteger succeedsEveryTenTimesCount = new AtomicInteger(0);

  @FlakyTest
  void neverFails() {
  }

  @FlakyTest(5)
  void succeedsEveryFourTimes() {
    if (succeedsEveryFourTimesCount.incrementAndGet() % 4 != 0) {
      fail("succeedsEveryFourTimes failed");
    }
  }

  @FlakyTest(15)
  void succeedsEveryTenTimes() {
    if (succeedsEveryTenTimesCount.incrementAndGet() % 10 != 0) {
      fail("succeedsEveryTenTimes failed");
    }
  }
}
