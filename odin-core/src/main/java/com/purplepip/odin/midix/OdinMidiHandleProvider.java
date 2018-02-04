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

import com.purplepip.odin.devices.Handle;
import com.purplepip.odin.devices.HandleProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

public class OdinMidiHandleProvider implements HandleProvider {

  @Override
  public Set<Handle> getIdentifiers() {
    Set<Handle> identifiers = new HashSet<>();
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      identifiers.add(new OdinMidiHandle(info));
    }
    return Collections.unmodifiableSet(identifiers);
  }
}
