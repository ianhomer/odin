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
   */
  public MidiSystemHelper logInfo() {
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      LOG.info("MIDI device info : {} ; {} ; {}", info.getVendor(), info.getName(),
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
      LOG.info("Found MIDI device : {} ; {}", midiDeviceMatcher, midiDevice.getClass().getName());
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
