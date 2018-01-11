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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import lombok.extern.slf4j.Slf4j;

/**
 * Convenience wrapper around the MidiSystem static to expose this as a bean that can be more
 * easily accessed with direct access to statics.
 */
@Slf4j
public class MidiSystemWrapper {
  private boolean extended = false;

  public MidiSystemWrapper extended() {
    extended = true;
    return this;
  }

  /**
   * Return a set of MIDI device infos.
   *
   * @return set of MIDI device infos
   */
  public Set<MidiDevice.Info> getMidiDeviceInfos() {
    Set<MidiDevice.Info> infos = new HashSet<>();
    Collections.addAll(infos, MidiSystem.getMidiDeviceInfo());
    return infos;
  }

  /**
   * Dump MIDI system information.
   */
  public void dump() {
    StringBuilder sb = new StringBuilder();
    sb.append("\nSYSTEM MIDI\n");
    sb.append("------------\n");
    sb.append(toString());
    sb.append('\n');
    LOG.info(sb.toString());
  }

  /**
   * MIDI system information to string.
   *
   * @return this MIDI system helper
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    if (infos.length == 0) {
      sb.append("No MIDI devices available");
    } else {
      sb.append("Devices\n");
      int i = 0;
      for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
        sb.append('\n').append(i).append(") ");
        i++;
        sb.append(" - ").append(info.getVendor());
        sb.append(" - ").append(info.getName());
        sb.append(" - ").append(info.getDescription());
        if (extended) {
          try {
            MidiDevice device = MidiSystem.getMidiDevice(info);
            sb.append("\n          μs position = ").append(device.getMicrosecondPosition());
            if (device instanceof Synthesizer) {
              sb.append(" - synthesizer latency = ")
                  .append(((Synthesizer) device).getLatency() / 1000).append("ms");
            }
            if (device.getMaxReceivers() != 0) {
              sb.append("\n               - receivers : max = ").append(device.getMaxReceivers());
              try (Receiver receiver = device.getReceiver()) {
                if (receiver != null) {
                  sb.append(" : default  = ").append(new MidiReceiverWrapper(receiver));
                }
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
              }
            }
            device.getTransmitters().forEach(transmitter ->
                sb.append(" ; transmitter = ")
                    .append(new MidiTransmitterWrapper(transmitter))
            );
          } catch (MidiUnavailableException e) {
            LOG.error("Cannot get device " + info, e);
          }
        }
      }
    }
    return sb.toString();
  }
}
