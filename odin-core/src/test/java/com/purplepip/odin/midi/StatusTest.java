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

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class StatusTest {
  @Test
  public void testValue() {
    assertEquals(-112, Status.NOTE_ON.getValue());
  }

  @Test
  public void testStatus() {
    assertEquals(Status.NOTE_ON, Status.getMessage((byte) 0x90));
    assertEquals(Status.NOTE_ON, Status.getMessage((byte) 0x91));
    assertEquals(Status.NOTE_OFF, Status.getMessage((byte) 0x83));
  }

  @Test
  public void testMessageByte() {
    assertEquals(144, Status.getMessageUnsignedInt((byte) 0x90));
    assertEquals(-112, Status.getMessageByte((byte) 0x90));
  }
}