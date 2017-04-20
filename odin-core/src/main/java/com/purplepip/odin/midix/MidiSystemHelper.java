package com.purplepip.odin.midix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.media.sound.JDK13Services;
import com.purplepip.odin.common.BeanUtils;
import com.purplepip.odin.common.OdinException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
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
  public void logInfo() {
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
   * Log MIDI system instruments available.
   */
  public void logInstruments() {
    Synthesizer synthesizer;
    try {
      synthesizer = MidiSystem.getSynthesizer();
      if (synthesizer != null) {
        Instrument[] instruments = synthesizer.getLoadedInstruments();
        for (int i = 0; i < instruments.length; i++) {
          LOG.debug("Synthesiser instruments (loaded) : % %", i, instruments[i].getName());
        }
        instruments = synthesizer.getAvailableInstruments();
        for (int i = 0; i < instruments.length; i++) {
          LOG.debug("Synthesiser instruments : {} {}", i, instruments[i].getName());
        }
        MidiChannel[] midiChannels = synthesizer.getChannels();
        for (int i = 0; i < midiChannels.length; i++) {
          LOG.debug("Synthesiser channels : {} {}", i, midiChannels[i].getProgram());
        }
      }
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get synthesizer", e);
    }
  }

  /**
   * Find a MIDI device by name.
   *
   * @param name Name of MIDI device to find
   * @return MIDI device
   * @throws OdinException
   */
  public MidiDevice findMidiDeviceByName(String name) throws OdinException {
    return findMidiDeviceByName(name, false);
  }

  /**
   * Find a MIDI device by name.
   *
   * @param name
   * @param exceptionOnNotFound
   * @return
   * @throws OdinException
   */
  public MidiDevice findMidiDeviceByName(String name, boolean exceptionOnNotFound) throws OdinException {
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

  private MidiDevice findMidiDeviceByNameInternal(String name, boolean exceptionOnNotFound) throws OdinException {
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
   * Find an instrument by name.
   *
   * @param name
   * @return
   * @throws MidiUnavailableException
   */
  public Instrument findInstrumentByName(String name) throws MidiUnavailableException {
    for (Instrument instrument : MidiSystem.getSynthesizer().getAvailableInstruments()) {
      if (instrument.getName().equals(name)) {
        return instrument;
      }
    }
    return null;
  }

  /**
   * Get an initialised device.
   *
   * @return
   * @throws OdinException
   */
  public MidiDevice getInitialisedDevice() throws OdinException {
    // TODO : Externalise and prioritise external MIDI devices to connect to.
    MidiDevice device = new MidiSystemHelper().findMidiDeviceByName("MidiMock IN");
    if (device == null) {
      device = new MidiSystemHelper().findMidiDeviceByName("Gervill");
    }
    LOG.debug("MIDI device : {}", device);

    if ("Gervill".equals(device.getDeviceInfo().getName())) {
      LOG.debug("Initialising internal synthesizer");
      try {
        // TODO : Externalise configuration - 41 is strings in internal Java engine
        device.getReceiver().send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 41, 0),
            -1);
      } catch (MidiUnavailableException | InvalidMidiDataException e) {
        LOG.error("Cannot change synthesizer instruments", e);
      }
    }
    return device;
  }
}
