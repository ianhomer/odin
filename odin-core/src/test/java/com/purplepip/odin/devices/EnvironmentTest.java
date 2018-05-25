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

package com.purplepip.odin.devices;

import static org.junit.Assert.assertEquals;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.configuration.Environments;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class EnvironmentTest {
  @Test
  public void testDump() {
    try (LogCaptor captor = new LogCapture().info().from(Environment.class).start()) {
      Environments.newEnvironment().dump();
      assertEquals("Environment log messages not as expected " + captor, 1, captor.size());
    }
  }
}