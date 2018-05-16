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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report information on the midi system.
 */
class MidiSystemHelper {
  private static final Logger LOG = LoggerFactory.getLogger(MidiSystemHelper.class);

  private final MidiSystemWrapper midiSystemWrapper = new MidiSystemWrapper();

  /**
   * Find a MIDI device by name.
   *
   * @param midiDeviceMatcher Matcher to match against
   * @return MIDI device
   * @throws OdinException Exception
   */
  private MidiDevice findMidiDeviceByName(MidiDeviceMatcher midiDeviceMatcher)
      throws OdinException {
    MidiDevice midiDevice = findMidiDeviceByNameInternal(midiDeviceMatcher);
    if (midiDevice != null) {
      LOG.debug("Found MIDI device : {} ; {}", midiDeviceMatcher, midiDevice.getClass().getName());
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

  MidiDevice getTransmittingDevice() throws OdinException {
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

  MidiDevice getReceivingDevice() throws OdinException {
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
  private static MidiDevice getInitialisedDevice(List<String> deviceNames,
      Function<String, MidiDeviceMatcher> midiDeviceMatcherFunction) throws OdinException {

    MidiDevice device = null;
    for (String deviceName : deviceNames) {
      device = new MidiSystemHelper()
          .findMidiDeviceByName(midiDeviceMatcherFunction.apply(deviceName));
      if (device != null) {
        break;
      }
    }

    if (device == null) {
      LOG.debug("Device not found for {}, falling back to default", midiDeviceMatcherFunction);
      /*
       * Note that we currently create the internal synthesizer device, but we are
       * careful later to not open it.  This seems a little fragile and could do with
       * improvement.
       */
      device = new MidiSystemHelper().findMidiDeviceByName(
          new MidiDeviceNameStartsWithMatcher("Gervill"));
    }

    if (device != null) {
      open(device);
    }

    LOG.debug("MIDI device : {}", device);
    return device;
  }

  private static void open(MidiDevice device) throws OdinException {
    if (canOpenWithWarnings(device)) {
      try {
        device.open();
      } catch (MidiUnavailableException e) {
        new AudioSystemWrapper().dump(true);
        throw new OdinException("Cannot open device " + device, e);
      }
    }
  }

  private static boolean canOpenWithWarnings(MidiDevice device) {
    AudioSystemWrapper audioSystemWrapper = new AudioSystemWrapper();
    if (device instanceof Synthesizer && !audioSystemWrapper.isAudioOutputSupported()) {
      LOG.warn("Cannot open synthesizer device when no mixers are available");
      new AudioSystemWrapper().dump(true);
      return false;
    }
    return true;
  }
}
