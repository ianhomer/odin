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

package com.purplepip.odin.perform;

import com.codahale.metrics.MetricFilter;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.demo.SimplePerformance;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.sequencer.TestSequencerEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Slf4j
@RunWith(Parameterized.class)
public class PerformanceTest {
  private Performance performance;
  private String testName;

  /**
   * Create performances test from injected parameter.
   *
   * @param parameter injected parameter
   */
  public PerformanceTest(PerformanceTestParameter parameter) {
    performance = parameter.performance();
    testName = performance.getClass().getSimpleName();
  }

  @Test
  public void testPerformance() throws OdinException, InterruptedException {
    final AtomicInteger operationCount = new AtomicInteger();
    OperationReceiver operationReceiver = (operation, time) -> operationCount.incrementAndGet();

    TestSequencerEnvironment environment =
        new TestSequencerEnvironment(operationReceiver, performance);

    LOG.debug("Spinning up : {}", testName);
    for (int i = 0 ; i < 200 ; i++) {
      environment.start();
      environment.shutdown();
    }

    environment.getConfiguration().getMetrics().removeMatching(MetricFilter.ALL);
    LOG.debug("Starting : {}", testName);
    environment.start();
    environment.shutdown();
    LOG.debug("Completed : {}", testName);
    LOG.info("Metrics : {}\n{}", testName,
        new MetricsReport(environment.getConfiguration().getMetrics()));
  }

  /**
   * Get parameters for parameterized tests.
   *
   * @return parameters
   */
  @Parameterized.Parameters
  public static Iterable<PerformanceTestParameter> parameters() {
    Collection<PerformanceTestParameter> parameters = new ArrayList<>();
    parameters.add(newParameter(new SimplePerformance()));
    parameters.add(newParameter(new GroovePerformance()));
    return parameters;
  }

  private static PerformanceTestParameter newParameter(Performance performance) {
    return new PerformanceTestParameter().performance(performance);
  }

  @Accessors(fluent = true)
  private static class PerformanceTestParameter {
    @Getter
    @Setter
    private Performance performance;
  }
}
