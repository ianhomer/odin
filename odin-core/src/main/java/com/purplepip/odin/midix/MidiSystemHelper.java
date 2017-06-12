/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report information on the midi system.
 */
public class MidiSystemHelper {
  private static final Logger LOG = LoggerFactory.getLogger(MidiSystemHelper.class);

  private MidiSystemWrapper midiSystemWrapper = new MidiSystemWrapper();

  /**
   * Log MIDI system info.
   *
   * @return this MIDI system helper
   */
  public MidiSystemHelper logInfo() {
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      LOG.debug("MIDI device info : {} ; {} ; {}", info.getVendor(), info.getName(),
          info.getDescription());
    }
    return this;
  }

  /**
   * Find a MIDI device by name.
   *
   * @param midiDeviceMatcher Matcher to match against
   * @param exceptionOnNotFound whether to throw an exception if not found
   * @return MIDI device
   * @throws OdinException Exception
   */
  private MidiDevice findMidiDeviceByName(MidiDeviceMatcher midiDeviceMatcher)
      throws OdinException {
    MidiDevice midiDevice = findMidiDeviceByNameInternal(midiDeviceMatcher);
    if (midiDevice != null) {
      LOG.debug("Found MIDI device : {} ; {}", midiDeviceMatcher, midiDevice.getClass().getName());
      try {
        midiDevice.open();
      } catch (MidiUnavailableException e) {
        throw new OdinException(e);
      }
    }
    return midiDevice;
  }

  private MidiDevice findMidiDeviceByNameInternal(MidiDeviceMatcher midiDeviceMatcher)
      throws OdinException {
    for (MidiDevice.Info info : midiSystemWrapper.getMidiDeviceInfos()) {
      if (midiDeviceMatcher.matches(info)) {
        MidiDevice deviceCandidate;
        try {
          deviceCandidate = MidiSystem.getMidiDevice(info);
        } catch (MidiUnavailableException e) {
          throw new OdinException(e);
        }

        if (midiDeviceMatcher.matches(deviceCandidate)) {
          return deviceCandidate;
        }
      }
    }
    return null;
  }

  /**
   * Get an initialised device.
   *
   * @return MIDI device
   * @throws OdinException Exception
   */
  MidiDevice getInitialisedDevice() throws OdinException {
    List<String> deviceNames = new ArrayList<>();
    deviceNames.add("Scarlett");
    deviceNames.add("USB");
    deviceNames.add("MidiMock IN");
    deviceNames.add("KEYBOARD");
    deviceNames.add("CTRL");

    MidiDevice device = null;
    for (String deviceName : deviceNames) {
      device = new MidiSystemHelper().findMidiDeviceByName(new MidiDeviceInMatcher(deviceName));
      if (device != null) {
        break;
      }
    }

    if (device == null) {
      device = new MidiSystemHelper().findMidiDeviceByName(
          new MidiDeviceNameStartsWithMatcher("Gervill"));
    }

    LOG.debug("MIDI device : {}", device);
    return device;
  }
}
