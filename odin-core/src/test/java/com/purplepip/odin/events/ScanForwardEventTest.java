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

package com.purplepip.odin.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.purplepip.odin.math.Rationals;
import com.purplepip.odin.math.Wholes;
import org.junit.jupiter.api.Test;

class ScanForwardEventTest {
  @Test
  void testGetValue() {
    assertNull(new ScanForwardEvent(Wholes.ZERO).getValue());
  }

  @Test
  void testGetTime() {
    ScanForwardEvent event = new ScanForwardEvent(Wholes.valueOf(9));
    assertEquals(Rationals.valueOf(9,1), event.getTime());
  }
}