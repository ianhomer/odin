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

import com.purplepip.odin.devices.AbstractHandle;
import com.purplepip.odin.devices.DeviceUnavailableException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
public class MidiHandle extends AbstractHandle {
  private final MidiDevice.Info deviceInfo;

  MidiHandle(MidiDevice.Info deviceInfo) {
    this.deviceInfo = deviceInfo;
    initialise();
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
  public OdinMidiDevice open() throws DeviceUnavailableException {
    try {
      MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);
      if (device instanceof Synthesizer) {
        return new SynthesizerDevice(this, (Synthesizer) device);
      } else {
        return new OdinMidiDevice(this, device);
      }
    } catch (MidiUnavailableException e) {
      throw new DeviceUnavailableException(e);
    }
  }

  @Override
  public String toString() {
    return getVendor() + " " + getName();
  }
}
