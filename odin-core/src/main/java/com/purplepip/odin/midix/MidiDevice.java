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

import com.purplepip.odin.audio.AudioSystemWrapper;
import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.clock.PerformanceTimeConverter;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.devices.AbstractDevice;
import com.purplepip.odin.devices.DeviceUnavailableException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MidiDevice extends AbstractDevice
    implements MicrosecondPositionProvider, MidiMessageReceiver {
  private final javax.sound.midi.MidiDevice device;
  private final MidiHandle handle;
  private final PerformanceTimeConverter timeConverter;

  MidiDevice(@NotNull MidiHandle handle, @NotNull javax.sound.midi.MidiDevice device)
      throws DeviceUnavailableException {
    this.device = device;
    this.handle = handle;
    timeConverter = new PerformanceTimeConverter(this);
    open();
    initialise();
  }

  public MidiHandle getHandle() {
    return handle;
  }



  @Override
  public boolean isOpen() {
    return device.isOpen();
  }

  @Override
  public boolean isSource() {
    return device.getMaxTransmitters() != 0;
  }

  @Override
  public boolean isSink() {
    return device.getMaxReceivers() != 0;
  }

  /*
   * TODO : Try to remove external access of MidiDevice, we want to encapsulate all internals
   * by defining an appropriate device interface.  I've marked it as deprecated since it's
   * only in place to help with a transition.
   */
  @Deprecated
  public javax.sound.midi.MidiDevice getMidiDevice() {
    return device;
  }

  @Override
  protected void initialise() {
    setProperty("microsecond.position", device.getMicrosecondPosition());
    setProperty("receivers.max", device.getMaxReceivers());
    setProperty("transmitters.max", device.getMaxTransmitters());
    if (device instanceof Synthesizer) {
      setProperty("synthesizer.latency", ((Synthesizer) device).getLatency() / 1000);
    }
    if (device.getMaxReceivers() != 0) {
      try {
        setProperty("receiver.default.name", device.getReceiver().getClass().getName());
      } catch (MidiUnavailableException e) {
        setProperty("receiver.default.exception", e.getMessage());
      }
      if (device.getReceivers().size() > 1) {
        int i = 0;
        for (Receiver receiver : device.getReceivers()) {
          setProperty("receiver", i, "name", receiver.getClass().getName());
        }
      }
    }
    if (device.getMaxTransmitters() != 0) {
      try {
        setProperty("transmitter.default.name", device.getTransmitter().getClass().getName());
      } catch (MidiUnavailableException e) {
        setProperty("transmitter.default.exception", e.getMessage());
      }
      if (device.getTransmitters().size() > 1) {
        int i = 0;
        for (Transmitter transmitter : device.getTransmitters()) {
          setProperty("transmitter", i, "name", transmitter.getClass().getName());
        }
      }
    }
  }

  @Override
  protected void deviceClose() {
    device.close();
  }

  @Override
  public String getName() {
    return getHandle().getName();
  }

  @Override
  public String getSummary() {
    StringBuilder sb = new StringBuilder();
    sb.append("μs position = ").append(device.getMicrosecondPosition());
    if (device instanceof Synthesizer) {
      sb.append(" - synthesizer latency = ")
          .append(getProperty("synthesizer.latency")).append("ms");
    }
    if (device.getMaxReceivers() != 0) {
      sb.append("\n               - receivers : max = ").append(device.getMaxReceivers());
      try (Receiver receiver = device.getReceiver()) {
        if (receiver != null) {
          sb.append(" : default  = ").append(getClassNameOrNullMessage(receiver));
        }
      } catch (MidiUnavailableException e) {
        sb.append(" : ").append(e.getMessage());
      }
    }
    device.getReceivers().forEach(receiver ->
        sb.append(" ; active receiver = ").append(getClassNameOrNullMessage(receiver))
    );
    if (device.getMaxTransmitters() != 0) {
      sb.append("\n               - transmitters : max = ")
          .append(device.getMaxTransmitters());
      try (Transmitter transmitter = device.getTransmitter()) {
        if (transmitter != null) {
          sb.append(" : default = ")
              .append(getClassNameOrNullMessage(transmitter));
        }
      } catch (MidiUnavailableException e) {
        sb.append(" : ").append(e.getMessage());
      }
    }
    device.getTransmitters().forEach(transmitter ->
        sb.append(" ; transmitter = ")
            .append(getClassNameOrNullMessage(transmitter))
    );
    return sb.toString();
  }

  private String getClassNameOrNullMessage(Object o) {
    return o == null ? "(null)" : o.getClass().getName();
  }

  @Override
  public String toString() {
    return device.getClass().getName() + " " + device.getDeviceInfo().getName();
  }

  protected void open() throws DeviceUnavailableException {
    try {
      device.open();
    } catch (MidiUnavailableException e) {
      new AudioSystemWrapper().dump(true);
      throw new DeviceUnavailableException("Cannot open device " + device, e);
    }
  }

  @Override
  public long getMicroseconds() {
    return device.getMicrosecondPosition();
  }

  /**
   * Send MIDI message.
   *
   * @param midiMessage MIDI message
   * @param time performance time
   */
  @Override
  public boolean send(MidiMessage midiMessage, long time) throws OdinException {
    try {
      if (isOpen()) {
        device.getReceiver().send(midiMessage, timeConverter.convert(time));
        return true;
      }
      LOG.warn("Message {} sent to closed device", midiMessage);
      return false;
    } catch (MidiUnavailableException e) {
      throw new OdinException("Cannot handle MIDI message for " + midiMessage, e);
    }
  }

  public void onPerformanceStart() {
    timeConverter.onPerformanceStart();
  }

  public void onPerformanceStop() {
    timeConverter.onPerformanceStop();
  }
}
