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

package com.purplepip.odin.midi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class DebugMessageTest {
  @Test
  public void testToString() throws Exception {
    byte[] bytes = new byte[] { -128, 60, 50};
    assertEquals("MIDI message [-128, 60, 50] with status (-128 = 0x80 = NOTE_OFF)",
        new DebugMessage(bytes).toString());
  }

  @Test
  public void testGetStatusAsString() throws Exception {
    byte[] bytes = new byte[] { -128, 60, 50};
    assertEquals("(-128 = 0x80 = NOTE_OFF)", new DebugMessage(bytes).getStatusAsString());
  }
}