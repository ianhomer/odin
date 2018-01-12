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

import com.google.common.io.BaseEncoding;
import java.util.Arrays;

/**
 * MIDI Message to help with debugging.
 */
public class DebugMessage {
  private final byte[] message;

  public DebugMessage(byte[] message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "MIDI message "
        + Arrays.toString(message)
        + " with status " + getStatusAsString();
  }

  /**
   * Get the message status as a debug string.
   *
   * @return message status as a debug string
   */
  public String getStatusAsString() {
    Status status = Status.getMessage(message[0]);
    if (status == null) {
      return "unknown " + message[0];
    }
    return "(" + status.getValue() + " = 0x" + BaseEncoding.base16().encode(message, 0, 1)
      + " = " + status.name() + ")";
  }
}
