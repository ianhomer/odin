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

package com.purplepip.odin.music.sequence;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.Loop;
import com.purplepip.odin.events.GenericEvent;
import com.purplepip.odin.performance.LoadPerformanceOperation;
import org.junit.jupiter.api.Test;

class LoaderTest {
  @Test
  void testLoader() {
    Loader loader = new Loader().performance("new-performance");
    loader.initialise();
    GenericEvent<LoadPerformanceOperation> event = loader.getNextEvent(null,
        new Loop(0));
    assertEquals("new-performance", event.getValue().getPerformanceUri().getSchemeSpecificPart());
  }
}