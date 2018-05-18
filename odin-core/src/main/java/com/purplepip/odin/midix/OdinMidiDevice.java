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

import com.purplepip.odin.devices.AbstractDevice;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

public class OdinMidiDevice extends AbstractDevice {
  private final MidiDevice device;

  OdinMidiDevice(MidiDevice device) {
    this.device = device;
    initialise();
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
  public String getName() {
    return device.getDeviceInfo().getName();
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
          sb.append(" : default  = ").append(new MidiReceiverWrapper(receiver));
        }
      } catch (MidiUnavailableException e) {
        sb.append(" : ").append(e.getMessage());
      }
    }
    device.getReceivers().forEach(receiver ->
        sb.append(" ; active receiver = ").append(new MidiReceiverWrapper(receiver))
    );
    if (device.getMaxTransmitters() != 0) {
      sb.append("\n               - transmitters : max = ")
          .append(device.getMaxTransmitters());
      try (Transmitter transmitter = device.getTransmitter()) {
        if (transmitter != null) {
          sb.append(" : default = ")
              .append(new MidiTransmitterWrapper(transmitter));
        }
      } catch (MidiUnavailableException e) {
        sb.append(" : ").append(e.getMessage());
      }
    }
    device.getTransmitters().forEach(transmitter ->
        sb.append(" ; transmitter = ")
            .append(new MidiTransmitterWrapper(transmitter))
    );
    return sb.toString();
  }
}
