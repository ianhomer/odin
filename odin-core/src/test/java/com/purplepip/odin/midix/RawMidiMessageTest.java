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

package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RawMidiMessageTest {
  @Test
  public void testRawMidiMessage() {
    byte[] buffer = new byte[] { (byte) 0x12, (byte) 11, (byte) 13 };

    RawMidiMessage message = new RawMidiMessage(buffer);
    assertEquals("Channel not correct", 2, message.getChannel());
    assertEquals("Command not correct",16, message.getCommand());
    assertEquals("Data1 not correct",11, message.getData1());
    assertEquals("Data2 not correct",13, message.getData2());
    assertEquals("Status not correct",18, message.getStatus());
  }
}