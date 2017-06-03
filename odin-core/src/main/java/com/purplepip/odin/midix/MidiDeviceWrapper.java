package com.purplepip.odin.midix;

import com.purplepip.odin.common.OdinException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
public class MidiDeviceWrapper implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(MidiDeviceWrapper.class);

  private MidiDevice device;
  private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

  public MidiDeviceWrapper() {
    this(false);
  }

  /**
   * Create a MIDI device wrapper.
   *
   * @param scan whether to support MIDI device change detection scanning
   */
  public MidiDeviceWrapper(boolean scan) {
    MidiDeviceScanner scanner = new MidiDeviceScanner();
    scanner.run();
    if (scan) {
      LOG.debug("MIDI Device scanning enabled");
      scheduledPool.scheduleWithFixedDelay(scanner, 0, 1, TimeUnit.SECONDS);
    }
  }

  public MidiDevice getDevice() {
    return device;
  }

  /**
   * Close device wrapper.
   */
  @Override
  public void close() {
    scheduledPool.shutdown();
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
    LOG.debug("Instrument name {} resolves to {} bank {} program {}", instrumentName,
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
  private void changeProgram(int channel, int bank, int program) {
    try {
      device.getReceiver().send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, program,
          bank >> 7),  -1);
    } catch (MidiUnavailableException | InvalidMidiDataException e) {
      LOG.error("Cannot change synthesizer instruments", e);
    }
    LOG.debug("Changed channel {} to program {}", channel, program);
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
    private Set<MidiDevice.Info> knownMidiDevices = new HashSet<>();

    @Override
    public void run() {
      // FIX : https://github.com/ianhomer/odin/issues/1
      LOG.debug("Scanning MIDI devices");
      Set<MidiDevice.Info> midiDevices = new MidiSystemWrapper().getMidiDeviceInfos();
      if (!midiDevices.equals(knownMidiDevices) || device == null) {
        LOG.debug("Refreshing MIDI device");
        knownMidiDevices = midiDevices;
        findDevice();
      }
    }

    private void findDevice() {
      try {
        device = new MidiSystemHelper().getInitialisedDevice();
      } catch (OdinException e) {
        LOG.error("Cannot initialise MIDI device", e);
      }
    }
  }
}
