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

import java.util.HashMap;
import java.util.Map;

/**
 * MIDI command.
 */
public enum Status {
  /*
   * See https://www.midi.org/specifications/item/table-1-summary-of-midi-message
   */
  NOTE_OFF(0x80),
  NOTE_ON(0x90),
  PROGRAM_CHANGE(0xC0);

  private byte value;
  private static Map<Byte, Status> values = new HashMap<>();

  static {
    for (int i = 0; i < values().length; i++) {
      Status value = values()[i];
      values.put(value.getValue(), value);
    }
  }

  Status(int value) {
    this.value = (byte) value;
  }

  public byte getValue() {
    return value;
  }

  public int asInt() {
    return value;
  }

  public static Status getMessage(byte value) {
    return values.get(value);
  }
}
