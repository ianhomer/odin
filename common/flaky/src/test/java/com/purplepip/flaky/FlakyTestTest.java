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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FlakyTestTest {
  private static final Logger LOG = LoggerFactory.getLogger(FlakyTestTest.class);

  @FlakyTest
  void reliable() {
    LOG.info("Executing reliable test");
    assertTrue(true);
  }

  @FlakyTest
  @Disabled
  void unreliable() {
    LOG.info("Executing unreliable test");
    throw new RuntimeException("mock exception");
  }
}
