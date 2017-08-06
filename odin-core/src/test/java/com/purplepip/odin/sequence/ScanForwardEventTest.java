/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.sequence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.purplepip.odin.math.Rational;
import org.junit.Test;

public class ScanForwardEventTest {
  @Test
  public void testGetValue() throws Exception {
    assertNull(new ScanForwardEvent<>(new Rational(0)).getValue());
  }

  @Test
  public void testGetTime() throws Exception {
    ScanForwardEvent event = new ScanForwardEvent<>(new Rational(9));
    assertEquals(9, event.getTime().getNumerator());
    assertEquals(1, event.getTime().getDenominator());
  }
}