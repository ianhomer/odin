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

import com.purplepip.odin.devices.Device;
import com.purplepip.odin.devices.Handle;

/**
 * Matcher to detect whether MIDI device matches condition.
 */
public interface MidiDeviceMatcher {
  /**
   * Match based on MIDI device info.
   *
   * @param handle Device handle to match on
   * @return true if MIDI device matches
   */
  boolean matches(Handle handle);

  /**
   * Match based on MIDI device.
   *
   * @param device MIDI device to match on
   * @return true if MIDI device matches
   */
  boolean matches(Device device);

  String getDescription();
}
