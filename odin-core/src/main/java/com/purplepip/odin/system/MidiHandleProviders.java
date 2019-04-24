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

package com.purplepip.odin.system;

import static com.purplepip.odin.devices.NamedHandle.asHandleList;
import static com.purplepip.odin.devices.NamedHandle.asHandleSet;

import com.purplepip.odin.midix.MidiHandleProvider;
import java.util.Collections;

public class MidiHandleProviders {

  /**
   * Create a new MIDI handle provider.
   *
   * @param audioEnabled whether audio is enabled or not
   * @return MIDI handle provider
   */
  public static MidiHandleProvider newMidiHandleProvider(boolean audioEnabled) {
    return new MidiHandleProvider(
        asHandleList("Scarlet", "FluidSynth", "USB", "Gervill"),
        asHandleList("Scarlet", "FluidSynth", "USB", "MidiMock OUT", "KEYBOARD", "CTRL"),
        // Exclude synthesizer if no audio handles present
        audioEnabled ? Collections.emptySet() : asHandleSet("Gervill")
    );
  }
}
