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

import static org.junit.Assert.assertTrue;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.demo.SimplePerformance;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.sequencer.TestSequencerEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
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
  private static final int LENIENCY_FACTOR = 2;
  private static final int EXCEL_FACTOR = 2;
  private static String environmentDescription;

  /*
   * Leniency factors for different environments.
   */
  private static Map<String, Integer> environmentFactors = new HashMap<>();
  /*
   * Default environment leniency factor for unknown system.
   */
  private static final int DEFAULT_ENVIRONMENT_FACTOR = 5;

  private String testName;
  private PerformanceTestParameter parameter;

  static {
    environmentDescription = generateEnvironmentDescription();
    /*
     * Explicitly register known environments that we've can give better estimate on performance.
     * Note that this is pretty crude, since this doesn't tell us how fast CPU is, nor how much
     * load it is already under.
     */
    // Local mac development
    environmentFactors.put("Mac OS X x86_64 x8", 1);
    // Travis build node
    environmentFactors.put("Linux amd64 x2", 2);
  }

  private static int getEnvironmentalFactor() {
    return environmentFactors.getOrDefault(environmentDescription, DEFAULT_ENVIRONMENT_FACTOR);
  }

  private static String generateEnvironmentDescription() {

    // TODO : When we shift to Java 9 we can get more info from ProcessHandle including total CPU
    // duration
    //System.getProperties().stringPropertyNames().forEach(name ->
    //    LOG.info("{} = {}", name, System.getProperty(name))
    //);
    //System.getenv().forEach((key, value) -> LOG.info("env : {} = {}", key, value));
    StringBuilder sb = new StringBuilder();
    sb.append(System.getProperty("os.name", "OS")).append(' ')
        .append(System.getProperty("os.arch", "CPU")).append(" x")
        .append(Runtime.getRuntime().availableProcessors());
    return sb.toString();
  }

  /**
   * Create performances test from injected parameter.
   *
   * @param parameter injected parameter
   */
  public PerformanceTest(PerformanceTestParameter parameter) {
    this.parameter = parameter;
    testName = parameter.performance().getClass().getSimpleName();
  }



  @Test
  public void testPerformance() throws OdinException, InterruptedException {
    final CountDownLatch latch = new CountDownLatch(parameter.operationCount());
    OperationReceiver operationReceiver = (operation, time) -> latch.countDown();

    TestSequencerEnvironment environment =
        new TestSequencerEnvironment(operationReceiver, parameter.performance());

    LOG.debug("Spinning up : {}", testName);
    for (int i = 0 ; i < 200 ; i++) {
      environment.start();
      Thread.sleep(10);
      environment.shutdown();
    }

    long start = System.nanoTime();
    environment.getConfiguration().getMetrics().removeMatching(MetricFilter.ALL);
    LOG.debug("Starting : {}", testName);
    environment.start();
    latch.await(5_000, TimeUnit.MILLISECONDS);
    environment.shutdown();
    long elapsed = System.nanoTime() - start;
    LOG.debug("Completed : {}", testName);
    MetricRegistry metrics = environment.getConfiguration().getMetrics();
    LOG.info("Metrics : {}\n{}", testName, new MetricsReport(metrics));
    LOG.info("Run time : {}ms\n", elapsed / 1_000_000, new MetricsReport(metrics));
    parameter.names().forEach(name -> assertTimer(name, parameter.expect(name),
        metrics.timer(name)));
  }

  private void assertTimer(String name, long expect, Timer timer) {
    long mean = (long) timer.getSnapshot().getMean();
    long maxAllowed = expect * LENIENCY_FACTOR * getEnvironmentalFactor();
    assertTrue(testName + "(" + environmentDescription + ") : Timer " + name + " too slow : "
            + mean + " > " + maxAllowed + " ; expected = " + expect,
        mean < maxAllowed);
    long excellentThreshold = expect / EXCEL_FACTOR;
    LOG.info("{} ({}) : Timer {} ; mean = {} ; expected = {} ; allowed = {}",
        testName, environmentDescription, name, mean, expect, maxAllowed);
    if (mean < excellentThreshold) {
      LOG.info("{} ({}) : Timer {} much faster than expected {}, "
          + " perhaps we can lower the assertion : "
          + "{} < {}", testName, environmentDescription, name, expect, mean, excellentThreshold);
    }
  }

  /**
   * Get parameters for parameterized tests.
   *
   * @return parameters
   */
  @Parameterized.Parameters
  public static Iterable<PerformanceTestParameter> parameters() {
    Collection<PerformanceTestParameter> parameters = new ArrayList<>();
    parameters.add(newParameter(new SimplePerformance())
        .operationCount(12)
        .expect("clock.prepare", 180_000)
        .expect("clock.start", 20_000)
        .expect("sequence.track.simple", 200_000));
    parameters.add(newParameter(new GroovePerformance())
        .operationCount(2_000)
        .expect("clock.start", 800_000)
        .expect("sequence.job", 400_000)
        .expect("sequence.track.kick3", 20_000)
        .expect("sequence.track.kick2", 14_000));
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

    @Setter
    @Getter
    private int operationCount = 100;

    /**
     * Map of timer names and expected time in nanoseconds.
     */
    private Map<String, Long> times = new HashMap<>();

    public PerformanceTestParameter expect(String name, long time) {
      times.put(name, time);
      return this;
    }

    public long expect(String name) {
      return times.get(name);
    }

    public Stream<String> names() {
      return times.keySet().stream();
    }
  }
}
