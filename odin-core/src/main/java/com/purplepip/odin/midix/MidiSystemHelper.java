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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.configuration.Environments;
import com.purplepip.odin.devices.Device;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.devices.Handle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * Report information on the midi system.
 */
@Slf4j
class MidiSystemHelper {

  /**
   * Find a MIDI device by name.
   *
   * @param midiDeviceMatcher Matcher to match against
   * @return MIDI device
   * @throws OdinException Exception
   */
  OdinMidiDevice findMidiDeviceByName(MidiDeviceMatcher midiDeviceMatcher)
      throws OdinException {
    OdinMidiDevice device = findMidiDeviceByNameInternal(midiDeviceMatcher);
    if (device != null) {
      LOG.debug("Found MIDI device : {} ; {}", midiDeviceMatcher, device);
    }
    return device;
  }

  private OdinMidiDevice findMidiDeviceByNameInternal(MidiDeviceMatcher midiDeviceMatcher)
      throws OdinException {
    for (Handle handle : Environments.newMidiEnvironment().getHandles()) {
      LOG.trace("Matching against {}", handle);
      if (midiDeviceMatcher.matches(handle)) {
        Device deviceCandidate;
        try {
          deviceCandidate = handle.open();
        } catch (DeviceUnavailableException e) {
          throw new OdinException(e);
        }

        if (midiDeviceMatcher.matches(deviceCandidate)) {
          return (OdinMidiDevice) deviceCandidate;
        }
      }
    }
    return null;
  }

  OdinMidiDevice getTransmittingDevice() throws OdinException {
    List<String> deviceNames = new ArrayList<>();
    /*
     * Priority list for discovering transmitting device, i.e. external devices that can send
     * MIDI signals to Odin.
     */
    deviceNames.add("Scarlett");
    deviceNames.add("USB");
    deviceNames.add("MidiMock OUT");
    deviceNames.add("KEYBOARD");
    deviceNames.add("CTRL");
    deviceNames.add("*");
    return getInitialisedDevice(deviceNames, MidiDeviceTransmitterMatcher::new);
  }

  OdinMidiDevice getReceivingDevice() throws OdinException {
    List<String> deviceNames = new ArrayList<>();
    /*
     * Priority list for discovering receiving device, i.e. external devices that can receive
     * MIDI signals from Odin.
     */
    deviceNames.add("Scarlett");
    deviceNames.add("FluidSynth");
    deviceNames.add("USB");
    return getInitialisedDevice(deviceNames, MidiDeviceReceiverMatcher::new);
  }

  /**
   * Get an initialised device.
   *
   * @return MIDI device
   * @throws OdinException Exception
   */
  private static OdinMidiDevice getInitialisedDevice(List<String> deviceNames,
      Function<String, MidiDeviceMatcher> midiDeviceMatcherFunction) throws OdinException {

    OdinMidiDevice device = null;
    for (String deviceName : deviceNames) {
      device = new MidiSystemHelper()
          .findMidiDeviceByName(midiDeviceMatcherFunction.apply(deviceName));
      if (device != null) {
        break;
      }
    }

    if (device == null) {
      LOG.debug("Device not found for matching {} against {}, falling back to default",
          midiDeviceMatcherFunction.apply("name"), deviceNames);
      /*
       * Note that we currently create the internal synthesizer device, but we are
       * careful later to not open it.  This seems a little fragile and could do with
       * improvement.
       */
      device = new MidiSystemHelper().findMidiDeviceByName(
          new MidiDeviceNameStartsWithMatcher("Gervill"));
    }

    if (device == null) {
      LOG.debug("Cannot find device matching {} against {}",
          midiDeviceMatcherFunction.apply("name"), deviceNames);
      return null;
    }

    LOG.debug("MIDI device : {}", device);
    return device;
  }
}
