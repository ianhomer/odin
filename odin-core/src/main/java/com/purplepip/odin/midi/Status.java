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
 * MIDI command.  Note that int representations of bytes are 0x01 = 1, 0x10 = 16, 0x7F = 127,
 * 0x80 = -128.
 */
public enum Status {
  /*
   * See https://www.midi.org/specifications/item/table-1-summary-of-midi-message
   */
  NOTE_OFF(0b1000_0000),    // 0x80 or 128
  NOTE_ON(0b1001_0000),     // 0x90 or 144
  CONTROL_CHANGE(0xB0),     // 176
  PROGRAM_CHANGE(0xC0);     // 192

  /*
   * We explicitly store this as a byte primarily to make it clear that it is only the byte part
   * of the value is significant for MIDI messages.
   */
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

  public static Status getMessage(byte value) {
    return values.get(getMessageByte(value));
  }

  /**
   * Get message byte for the given value.
   *
   * @param value value to get message byte for
   * @return byte
   */
  static byte getMessageByte(byte value) {
    /*
     * As per MIDI specification system messages have first 4 bits set.  Otherwise it is a channel
     * message with the first 4 bits specifying the status and the last 4 bits specifying the
     * channel.
     */
    if (value >>> 4 != 16) {
      return (byte) (value & 0xF0);
    }
    return value;
  }

  static int getMessageUnsignedInt(byte value) {
    return getMessageByte(value) & 0xFF;
  }
}
