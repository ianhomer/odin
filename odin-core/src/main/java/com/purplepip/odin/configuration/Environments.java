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

package com.purplepip.odin.configuration;

import com.purplepip.odin.audio.AudioHandleProvider;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.midix.MidiHandleProvider;

public final class Environments {
  private Environments() {
  }

  /**
   * Create the default environment for the Odin configuration.
   *
   * @return new default environment.
   */
  public static Environment newEnvironment() {
    return new Environment(
        new MidiHandleProvider(),
        new AudioHandleProvider()
    );
  }

  /**
   * Create a new environment with just MIDI devices.
   *
   * @return new environment with just MIDI devices
   */
  public static Environment newMidiEnvironment() {
    return new Environment(
        new MidiHandleProvider()
    );
  }
}
