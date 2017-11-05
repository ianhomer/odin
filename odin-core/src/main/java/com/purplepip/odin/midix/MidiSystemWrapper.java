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
import lombok.extern.slf4j.Slf4j;

/**
 * Convenience wrapper around the MidiSystem static to expose this as a bean that can be more
 * easily accessed with direct access to statics.
 */
@Slf4j
public class MidiSystemWrapper {
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
        sb.append('\n').append(i++).append(") ");
        sb.append(" - ").append(info.getVendor());
        sb.append(" - ").append(info.getName());
        sb.append(" - ").append(info.getDescription());
      }
    }
    return sb.toString();
  }
}
