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
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matches MIDI device in.
 */
@ToString(callSuper = true)
public class MidiDeviceTransmitterMatcher extends MidiDeviceNameStartsWithMatcher {
  private static final Logger LOG = LoggerFactory.getLogger(MidiDeviceTransmitterMatcher.class);

  MidiDeviceTransmitterMatcher(String prefix) {
    super(prefix);
  }

  @Override
  public boolean matches(Device device) {
    if (!(device instanceof MidiDevice)) {
      return false;
    }
    javax.sound.midi.MidiDevice midiDevice = ((MidiDevice) device).getMidiDevice();
    LOG.debug("Device {} max transmitters {}", device.getName(),
        midiDevice.getMaxTransmitters());
    boolean result = midiDevice.getMaxTransmitters() != 0;
    LOG.debug("Device {} match {}", device.getName(), result);
    return result && super.matches(device);
  }
}
