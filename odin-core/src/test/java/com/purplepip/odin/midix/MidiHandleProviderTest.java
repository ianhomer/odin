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

import static com.purplepip.odin.devices.NamedHandle.asHandleList;
import static com.purplepip.odin.devices.NamedHandle.asHandleSet;
import static com.purplepip.odin.system.MidiHandleProviders.newMidiHandleProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.devices.Handle;
import com.purplepip.odin.system.Environments;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MidiHandleProviderTest {
  @Test
  public void shouldHandleStreamHaveSequencer() {
    MidiHandleProvider provider = new MidiHandleProvider(
        asHandleList("Scarlet", "FluidSynth", "USB", "Real Time Sequencer"),
        asHandleList("Scarlet", "FluidSynth", "USB", "MidiMock OUT", "KEYBOARD", "CTRL"),
        asHandleSet("Gervill")
    );
    LOG.debug("Sinks : {}", provider.getSinkHandles());
    Optional<Handle> handle = provider.findOneSink();
    assertTrue(handle.isPresent());
    assertEquals("Real Time Sequencer", handle.get().getName());
  }

  @Test
  public void testChangeProgramName() throws DeviceUnavailableException {
    assumeTrue(Environments.isAudioEnabled());
    LOG.debug("Testing changing synthesizer program ...");
    MidiHandleProvider provider = newMidiHandleProvider(Environments.isAudioEnabled());
    assertTrue(provider.findAllSinks().count() > 0);
    Handle handle = provider.findFirstSinkByClassOrElseThrow(SynthesizerDevice.class);
    assertEquals("Gervill", handle.getName());
    LOG.debug("Testing changing synthesizer program on {}", handle);
  }
}