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

import com.purplepip.odin.performance.Performance;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class PerformanceTestParameter {
  private static final int DEFAULT_STATIC_BEATS_PER_MINUTE = 6000;
  private static final int DEFAULT_EXPECTED_OPERATION_COUNT = 20;
  private static final long DEFAULT_WAIT = 2000;

  static PerformanceTestParameter newParameter(Performance performance) {
    return new PerformanceTestParameter().performance(performance);
  }

  @Getter @Setter
  private Performance performance;

  @Getter @Setter
  private int staticBeatsPerMinute = DEFAULT_STATIC_BEATS_PER_MINUTE;

  @Getter @Setter
  private int expectedOperationCount = DEFAULT_EXPECTED_OPERATION_COUNT;

  @Getter @Setter
  private long testWait = DEFAULT_WAIT;

  PerformanceTestParameter[] asArray() {
    return new PerformanceTestParameter[] { this };
  }
}
