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
import com.purplepip.odin.midi.RawMessage;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider of a MIDI device.
 */
public class MidiDeviceWrapper implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(MidiDeviceWrapper.class);

  private MidiDevice receivingDevice;
  private MidiDevice transmittingDevice;
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

  public MidiDevice getReceivingDevice() {
    return receivingDevice;
  }

  public MidiDevice getTransmittingDevice() {
    return transmittingDevice;
  }

  /**
   * Close device wrapper.
   */
  @Override
  public void close() {
    LOG.debug("Closing device wrapper");
    scheduledPool.shutdown();
  }

  /**
   * Change channel to first instrument found that contains the given instrument name string.
   *
   * @param channel channel to change
   * @param instrumentName instrument name to search for
   * @throws OdinException exception
   */
  public Instrument changeProgram(int channel, String instrumentName) throws OdinException {
    Instrument instrument = findInstrument(channel, instrumentName);
    changeProgram(channel, instrument.getPatch().getBank(), instrument.getPatch().getProgram());
    return instrument;
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
      receivingDevice.getReceiver().send(
          new RawMidiMessage(new RawMessage(
              new ProgramChangeOperation(channel, bank, program)).getBytes()),
          -1);
    } catch (MidiUnavailableException | OdinException e) {
      LOG.error("Cannot change synthesizer instruments", e);
    }
    LOG.debug("Changed channel {} to program {}", channel, program);
  }

  /**
   * Find instrument.
   *
   * @param channel channel
   * @param instrumentName instrument name
   * @return instrument
   * @throws OdinException exception
   */
  public Instrument findInstrument(int channel, String instrumentName) throws OdinException {
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
    return instrument;
  }

  /**
   * Check whether this is the internal Java Gervill synthesizer.
   *
   * @return true if this is the internal Java Gervill synthesizer
   */
  public boolean isGervill() {
    return "Gervill".equals(receivingDevice.getDeviceInfo().getName());
  }

  /**
   * Check whether device is a local synthesizer.
   *
   * @return true if this is a local synthesizer
   */
  public boolean isSynthesizer() {
    return receivingDevice instanceof Synthesizer;
  }

  public Synthesizer getSynthesizer() {
    return (Synthesizer) receivingDevice;
  }

  public boolean canTransmit() {
    return transmittingDevice != null && transmittingDevice.getMaxTransmitters() != 0;
  }

  class MidiDeviceScanner implements Runnable {
    private Set<MidiDevice.Info> knownMidiDevices = new HashSet<>();
    private MidiSystemHelper helper = new MidiSystemHelper();

    @Override
    public void run() {
      // FIX : https://github.com/ianhomer/odin/issues/1
      LOG.debug("Scanning MIDI devices");
      Set<MidiDevice.Info> midiDevices = new MidiSystemWrapper().getMidiDeviceInfos();
      if (!midiDevices.equals(knownMidiDevices)
          || receivingDevice == null || transmittingDevice == null) {
        LOG.debug("Refreshing MIDI device");
        knownMidiDevices = midiDevices;
        findDevice();
      }
    }

    private void findDevice() {
      try {
        receivingDevice = helper.getReceivingDevice();
      } catch (OdinException e) {
        LOG.error("Cannot initialise receiving MIDI device", e);
      }
      try {
        transmittingDevice = helper.getTransmittingDevice();
      } catch (OdinException e) {
        LOG.error("Cannot initialise transmitting MIDI device", e);
      }
    }
  }
}
