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

import javax.sound.midi.MidiDevice;
import lombok.ToString;

/**
 * Match MIDI devices where the name matches the given starts with string.
 */
@ToString
public class MidiDeviceNameStartsWithMatcher implements MidiDeviceMatcher {
  private static final String MATCH_ALL = "*";
  private String prefix;

  public MidiDeviceNameStartsWithMatcher(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public boolean matches(MidiDevice.Info info) {
    return MATCH_ALL.equals(prefix) || info.getName().startsWith(prefix);
  }

  @Override
  public boolean matches(MidiDevice device) {
    return matches(device.getDeviceInfo());
  }

  @Override
  public String getDescription() {
    return "Starts with " + prefix;
  }
}
