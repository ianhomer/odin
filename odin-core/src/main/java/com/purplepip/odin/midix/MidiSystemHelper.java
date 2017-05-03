package com.purplepip.odin.midix;

import com.purplepip.odin.common.BeanUtils;
import com.purplepip.odin.common.OdinException;
import com.sun.media.sound.JDK13Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.spi.MidiDeviceProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report information on the midi system.
 */
public class MidiSystemHelper {
  private static final Logger LOG = LoggerFactory.getLogger(MidiSystemHelper.class);

  /**
   * Log MIDI system info.
   */
  public MidiSystemHelper logInfo() {
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      LOG.info("MIDI device info : {} ; {} ; {}", info.getVendor(), info.getName(),
          info.getDescription());
    }

    List<?> list = JDK13Services.getProviders(MidiDeviceProvider.class);
    for (Object midiDeviceProvider : list) {
      if (midiDeviceProvider instanceof MidiDeviceProvider) {
        log((MidiDeviceProvider) midiDeviceProvider);
      }
    }
    return this;
  }

  /**
   * Log the info for a given MIDI device provider.
   *
   * @param midiDeviceProvider MIDI device provider
   */
  public void log(MidiDeviceProvider midiDeviceProvider) {
    new BeanUtils().dumpStaticMethodResponse(midiDeviceProvider.getClass(), "nGetNumDevices");
    for (MidiDevice.Info info : midiDeviceProvider.getDeviceInfo()) {
      LOG.info("{} : {}", midiDeviceProvider.getClass(), info);
    }
  }

  /**
   * Find a MIDI device by name.
   *
   * @param midiDeviceMatcher Matcher to match against
   * @return MIDI device
   * @throws OdinException Exception.
   */
  public MidiDevice findMidiDeviceByName(MidiDeviceMatcher midiDeviceMatcher) throws OdinException {
    return findMidiDeviceByName(midiDeviceMatcher, false);
  }

  /**
   * Find a MIDI device by name.
   *
   * @param midiDeviceMatcher Matcher to match against
   * @param exceptionOnNotFound whether to throw an exception if not found
   * @return MIDI device
   * @throws OdinException Exception
   */
  public MidiDevice findMidiDeviceByName(MidiDeviceMatcher midiDeviceMatcher,
                                         boolean exceptionOnNotFound)
      throws OdinException {
    MidiDevice midiDevice = findMidiDeviceByNameInternal(midiDeviceMatcher, exceptionOnNotFound);
    if (midiDevice != null) {
      LOG.info("Found MIDI device : " + midiDeviceMatcher + " ; "
          + midiDevice.getClass().getName());
      try {
        midiDevice.open();
      } catch (MidiUnavailableException e) {
        throw new OdinException(e);
      }
    }
    return midiDevice;
  }

  private MidiDevice findMidiDeviceByNameInternal(MidiDeviceMatcher midiDeviceMatcher,
                                                  boolean exceptionOnNotFound)
      throws OdinException {
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
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
    if (exceptionOnNotFound) {
      throw new OdinException("Cannot find midi device " + midiDeviceMatcher.getDescription());
    }
    return null;
  }

  /**
   * Get an initialised device.
   *
   * @return MIDI device
   * @throws OdinException Exception
   */
  public MidiDevice getInitialisedDevice() throws OdinException {
    // TODO : Externalise and prioritise external MIDI devices to connect to.
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
