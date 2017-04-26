package com.purplepip.odin.midix;

import com.purplepip.odin.common.BeanUtils;
import com.purplepip.odin.common.OdinException;
import com.sun.media.sound.JDK13Services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
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
   * Return a set of MIDI device infos.
   *
   * @return set of MIDI device infos
   */
  public Set<MidiDevice.Info> getMidiDeviceInfos() {
    Set<MidiDevice.Info> infos = new HashSet<>();
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      infos.add(info);
    }
    return infos;
  }



  /**
   * Find a MIDI device by name.
   *
   * @param name Name of MIDI device to find
   * @return MIDI device
   * @throws OdinException Exception.
   */
  public MidiDevice findMidiDeviceByName(String name) throws OdinException {
    return findMidiDeviceByName(name, false);
  }

  /**
   * Find a MIDI device by name.
   *
   * @param name Name of device to find
   * @param exceptionOnNotFound whether to throw an exception if not found
   * @return MIDI device
   * @throws OdinException Exception
   */
  public MidiDevice findMidiDeviceByName(String name, boolean exceptionOnNotFound)
      throws OdinException {
    MidiDevice midiDevice = findMidiDeviceByNameInternal(name, exceptionOnNotFound);
    if (midiDevice != null) {
      LOG.info("Found MIDI device : " + name + " ; " + midiDevice.getClass().getName());
      try {
        midiDevice.open();
      } catch (MidiUnavailableException e) {
        throw new OdinException(e);
      }
    }
    return midiDevice;
  }

  private MidiDevice findMidiDeviceByNameInternal(String name, boolean exceptionOnNotFound)
      throws OdinException {
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      if (info.getName().equals(name)) {
        try {
          return MidiSystem.getMidiDevice(info);
        } catch (MidiUnavailableException e) {
          throw new OdinException(e);
        }
      }
    }
    if (exceptionOnNotFound) {
      throw new OdinException("Cannot find midi device " + name);
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
    MidiDevice device = new MidiSystemHelper().findMidiDeviceByName("MidiMock IN");
    if (device == null) {
      device = new MidiSystemHelper().findMidiDeviceByName("Gervill");
    }
    LOG.debug("MIDI device : {}", device);

    return device;
  }
}
