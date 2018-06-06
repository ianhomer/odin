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

import com.purplepip.odin.clock.PerformanceListener;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.sequencer.OperationTransmitter;
import com.purplepip.odin.system.Environments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider of a MIDI device.
 */
public class MidiDeviceWrapper implements AutoCloseable, PerformanceListener {
  private static final Logger LOG = LoggerFactory.getLogger(MidiDeviceWrapper.class);

  private MidiDevice receivingDevice;
  private MidiDevice transmittingDevice;
  private final MidiTransmitterBinding midiTransmitterBinding = new MidiTransmitterBinding();

  /**
   * Create a MIDI device wrapper.
   */
  public MidiDeviceWrapper() {
    final MidiDeviceFinder finder = new MidiDeviceFinder();
    try {
      finder.find();
    } catch (OdinException e) {
      /*
       * TODO : Not being able to initialise the device should be fatal, however to do that
       * we need to support a stub mode for -PnoAudio mode / audio not available (on build machine)
       */
      LOG.warn("Cannot initialise MIDI device : {}", e.getMessage());
    }
  }

  public MidiDevice getReceivingDevice() {
    return receivingDevice;
  }

  public MidiDevice getTransmittingDevice() {
    return transmittingDevice;
  }

  /**
   * Register this MIDI device with the operation transmitter, so that incoming MIDI messages
   * are sent to the given transmitter.
   *
   * @param operationTransmitter operation transmitter to relay MIDI messages on to
   */
  public void registerWithTransmitter(OperationTransmitter operationTransmitter) {
    midiTransmitterBinding.bind(getTransmittingDevice(), operationTransmitter);

  }

  /**
   * Close device wrapper.
   */
  @Override
  public void close() {
    LOG.debug("Closing device wrapper");
    midiTransmitterBinding.close();
  }

  /**
   * Check whether device is a local synthesizer.
   *
   * @return true if this is a local synthesizer
   */
  public boolean isSynthesizer() {
    return receivingDevice instanceof SynthesizerDevice;
  }

  public SynthesizerDevice getSynthesizer() {
    return (SynthesizerDevice) receivingDevice;
  }

  public boolean canTransmit() {
    return transmittingDevice != null && transmittingDevice.canTransmit();
  }

  @Override
  public void onPerformanceStart() {
    getReceivingDevice().onPerformanceStart();
  }

  @Override
  public void onPerformanceStop() {
    getReceivingDevice().onPerformanceStop();
  }

  class MidiDeviceFinder {
    private Environment environment = Environments.newEnvironment();

    public void find() throws OdinException {
      LOG.debug("Refreshing MIDI device");
      findDevice();
    }

    private void findDevice() throws OdinException {
      MidiHandle transmittingHandle = (MidiHandle) environment
          .findOneSourceHandle(MidiHandle.class).orElse(null);
      if (transmittingHandle != null) {
        transmittingDevice = transmittingHandle.open();
      }
      MidiHandle receivingHandle = (MidiHandle) environment
          .findOneSinkHandle(MidiHandle.class).orElse(null);
      if (receivingHandle != null) {
        receivingDevice = receivingHandle.open();
      }
    }
  }
}
