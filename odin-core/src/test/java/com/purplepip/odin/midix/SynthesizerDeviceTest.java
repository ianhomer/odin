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

import static com.purplepip.odin.system.Environments.newAudioEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.system.Environments;
import javax.sound.midi.Instrument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SynthesizerDeviceTest {
  private SynthesizerDevice synthesizerDevice;

  /**
   * Set up test.
   *
   * @throws DeviceUnavailableException device unavailable
   */
  @BeforeAll
  void setUp() throws DeviceUnavailableException {
    assumeTrue(!newAudioEnvironment().isEmpty());
    Environment environment = Environments.newEnvironment();
    synthesizerDevice = (SynthesizerDevice) environment.findOneSinkOrThrow(MidiHandle.class);
    assumeTrue(synthesizerDevice.isOpen());
  }

  @Test
  void testFindInstrument() {
    Instrument instrument = synthesizerDevice.findInstrumentByName("tubular", false);
    assertNotNull(instrument);
    assertEquals("Cannot find Tubular Bells","Tubular Bells", instrument.getName());
  }

  @Test
  void testFindDrumkit() {
    Instrument instrument = synthesizerDevice.findInstrumentByName("standard kit", true);
    assertNotNull(instrument);
    assertEquals("Cannot find Standard Kit","Standard Kit", instrument.getName());
  }

  @Test
  void testFindMissingInstrument() {
    Instrument instrument = synthesizerDevice
        .findInstrumentByName("non-existing-instrument", false);
    assertNull("Cannot find Tubular Bells", instrument);
  }

  @Test
  void testLogInstruments() {
    try (LogCaptor captor = new LogCapture().debug().from(SynthesizerDevice.class).start()) {
      synthesizerDevice.logInstruments();
      assertTrue("Not enough messages logged", captor.size() > 10);
    }
  }

  @Test
  void testLoadSoundbank() {
    try (LogCaptor captor = new LogCapture().start()) {
      boolean result = synthesizerDevice.loadGervillSoundBank(
          "soundbank-emg.sf2");
      assertTrue("Cannot load emergency soundbank", result);
      assertEquals(1, captor.size());
    }
  }

  @Test
  void testLoadMissingSoundbank() {
    try (LogCaptor logCapture = new LogCapture().start()) {
      boolean result = synthesizerDevice.loadGervillSoundBank(
          "soundbank-that-is-missing.sf2");
      assertFalse("Should not be able to load missing soundbank", result);
      assertEquals(1, logCapture.size());
    }
  }
}
