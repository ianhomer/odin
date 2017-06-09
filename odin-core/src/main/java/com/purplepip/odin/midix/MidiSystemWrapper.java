/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

/**
 * Convenience wrapper around the MidiSystem static to expose this as a bean that can be more
 * easily accessed with direct access to statics.
 */
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
}
