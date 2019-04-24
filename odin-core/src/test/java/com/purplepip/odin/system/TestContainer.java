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

package com.purplepip.odin.system;

import lombok.extern.slf4j.Slf4j;

/**
 * Singleton describing the container tests that odin is running in.
 */
@Slf4j
public class TestContainer {
  private static TestContainer testContainer = new TestContainer();

  /*
   * Speed factor.
   */
  private static final String SPEED_FACTOR = "odin.test.speed";

  private int speedFactor;

  TestContainer() {
    speedFactor = Integer.parseInt(System.getProperty(SPEED_FACTOR, "1"));
    if (speedFactor > 1) {
      LOG.info("Test speed factor : {}", speedFactor);
    }
  }

  public static TestContainer getContainer() {
    return testContainer;
  }

  /**
   * Speed up by the system configured speed factor.
   *
   * @param speed default speed
   * @return speed factored speed
   */
  public static int factorSpeed(int speed) {
    return speed * getContainer().getSpeedFactor();
  }

  /**
   * Reduce time according to the speed factor.
   *
   * @param time time to reduce
   * @return speed factor reduced time
   */
  public static int factorTime(int time) {
    return time / getContainer().getSpeedFactor();
  }

  /**
   * Speed factor for the tests.  Increasing this number allows sequencing in tests to
   * be run with higher BPM and less delays and hence allows testing of timing reliability
   * issues.
   *
   * @return speed factor.
   */
  public int getSpeedFactor() {
    return speedFactor;
  }
}
