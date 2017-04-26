package com.purplepip.odin.midix;

import com.purplepip.odin.common.OdinException;

import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider of a MIDI device.
 */
public class MidiDeviceWrapper {
  private static final Logger LOG = LoggerFactory.getLogger(MidiDeviceWrapper.class);

  private MidiDevice device;
  private MidiDeviceScanner scanner;

  public MidiDeviceWrapper() {
    this(false);
  }

  /**
   * Create a MIDI device wrapper.
   *
   * @param scan whether to support MIDI device change detection scanning
   */
  public MidiDeviceWrapper(boolean scan) {
    if (scan) {
      LOG.info("MIDI Device scanning enabled");
      scanner = new MidiDeviceScanner();
      Thread thread = new Thread(scanner);
      thread.start();
      while (device == null) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          LOG.error("Thread interrupted", e);
        }
      }
    } else {
      findDevice();
    }
  }

  public MidiDeviceWrapper(MidiDevice device) {
    this.device = device;
  }

  public MidiDevice getDevice() {
    return device;
  }

  /**
   * Close device wrapper.
   */
  public void close() {
    if (scanner != null) {
      scanner.stop();
    }
  }

  protected void findDevice() {
    try {
      device = new MidiSystemHelper().getInitialisedDevice();
    } catch (OdinException e) {
      LOG.error("Cannot initialise MIDI device", e);
    }
  }

  /**
   * Change channel to first instrument found that contains the given instrument name string.
   *
   * @param channel channel to change
   * @param instrumentName instrument name to search for
   * @throws OdinException exception
   */
  public void changeProgram(int channel, String instrumentName) throws OdinException {
    if (!isSynthesizer()) {
      throw new OdinException("Cannot search for instrument name if not local synthesizer");
    }
    Instrument instrument = new SynthesizerHelper(getSynthesizer())
        .findInstrumentByName(instrumentName, channel == 9);
    if (instrument == null) {
      throw new OdinException("Cannot find instrument " + instrumentName);
    }
    LOG.info("Instrument name {} resolves to {} bank {} program {}", instrumentName,
        instrument.getName(),
        instrument.getPatch().getBank(), instrument.getPatch().getProgram());
    changeProgram(channel, instrument.getPatch().getBank(), instrument.getPatch().getProgram());
  }

  /**
   * Change program via a MIDI program change message.
   *
   * @param channel channel to change
   * @param program program to set
   */
  public void changeProgram(int channel, int program) {
    changeProgram(channel, 0, program);
  }

  /**
   * Change program via a MIDI program change message.
   *
   * @param channel channel to change
   * @param bank bank to set
   * @param program program to set
   */
  public void changeProgram(int channel, int bank, int program) {
    try {
      device.getReceiver().send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, program,
          bank >> 7),  -1);
    } catch (MidiUnavailableException | InvalidMidiDataException e) {
      LOG.error("Cannot change synthesizer instruments", e);
    }
    LOG.info("Changed channel {} to program {}", channel, program);
  }

  /**
   * Check whether this is the internal Java Gervill synthesizer.
   *
   * @return true if this is the internal Java Gervill synthesizer
   */
  public boolean isGervill() {
    return "Gervill".equals(device.getDeviceInfo().getName());
  }

  /**
   * Check whether device is a local synthesizer.
   *
   * @return true if this is a local synthesizer
   */
  public boolean isSynthesizer() {
    return device instanceof Synthesizer;
  }

  public Synthesizer getSynthesizer() {
    return (Synthesizer) device;
  }


  class MidiDeviceScanner implements Runnable {
    private boolean exit;
    private Set<MidiDevice.Info> knownMidiDevices = new HashSet<>();

    @Override
    public void run() {
      while (!exit) {
        // FIX : https://github.com/ianhomer/odin/issues/1
        LOG.debug("Scanning MIDI devices");

        new MidiSystemHelper().logInfo();
        Set<MidiDevice.Info> midiDevices = new MidiSystemWrapper().getMidiDeviceInfos();
        if (!midiDevices.equals(knownMidiDevices) || device == null) {
          LOG.debug("Refreshing MIDI device");
          knownMidiDevices = midiDevices;
          findDevice();
        }

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          LOG.error("Thread interrupted", e);
        }
      }
    }

    public void stop() {
      exit = true;
    }
  }
}
