package com.purplepip.odin.midix;

import com.purplepip.odin.common.OdinException;

import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.MidiDevice;

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

  public void close() {
    scanner.stop();
  }

  protected void findDevice() {
    try {
      device = new MidiSystemHelper().getInitialisedDevice();
    } catch (OdinException e) {
      LOG.error("Cannot initialise MIDI device", e);
    }
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
        Set<MidiDevice.Info> midiDevices = new MidiSystemHelper().getMidiDeviceInfos();
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
