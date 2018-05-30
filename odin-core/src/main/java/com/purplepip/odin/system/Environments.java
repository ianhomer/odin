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

import com.purplepip.odin.audio.AudioHandleProvider;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.devices.HandleProvider;
import com.purplepip.odin.midix.MidiHandleProvider;
import java.util.stream.Stream;

public final class Environments {
  private Environments() {
  }

  /**
   * Create a new environment.
   *
   * @param container container to base environment on.
   * @return environment
   */
  public static Environment newEnvironment(Container container) {
    return new Environment(
        withAudioHandleProvider(container, Stream.builder())
            .add(newMidiHandleProvider())
            .build()
    );
  }


  /**
   * Create the default environment for the Odin configuration.
   *
   * @return new default environment.
   */
  public static Environment newEnvironment() {
    return newEnvironment(SystemContainer.getContainer());
  }

  private static Stream.Builder<HandleProvider>
      withAudioHandleProvider(Container container, Stream.Builder<HandleProvider> builder) {
    if (container.isAudioEnabled()) {
      builder.accept(new AudioHandleProvider());
    }
    return builder;
  }

  /**
   * Create a new environment with just MIDI devices.
   *
   * @return new environment with just MIDI devices
   */
  public static Environment newMidiEnvironment() {
    return new Environment(
        newMidiHandleProvider()
    );
  }

  private static MidiHandleProvider newMidiHandleProvider() {
    return new MidiHandleProvider(
        asHandleList(
            "Scarlet", "FluidSynth", "USB", "MidiMock OUT", "KEYBOARD", "CTRL")
    );
  }

  public static Environment newAudioEnvironment(Container container) {
    return new Environment(withAudioHandleProvider(
        container, Stream.builder()).build());
  }

  public static Environment newAudioEnvironment() {
    return newAudioEnvironment(SystemContainer.getContainer());
  }
}
