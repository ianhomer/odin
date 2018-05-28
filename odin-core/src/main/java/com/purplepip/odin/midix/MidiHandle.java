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
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.devices.Handle;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class MidiHandle implements Handle {
  private final MidiDevice.Info deviceInfo;

  MidiHandle(MidiDevice.Info deviceInfo) {
    this.deviceInfo = deviceInfo;
  }

  @Override
  public String getName() {
    return deviceInfo.getName();
  }

  @Override
  public String getVendor() {
    return deviceInfo.getVendor();
  }

  @Override
  public String getDescription() {
    return deviceInfo.getDescription();
  }

  @Override
  public String getType() {
    return "MIDI";
  }

  @Override
  public Device open() throws DeviceUnavailableException {
    try {
      return new OdinMidiDevice(MidiSystem.getMidiDevice(deviceInfo));
    } catch (MidiUnavailableException e) {
      throw new DeviceUnavailableException(e);
    }
  }
}
