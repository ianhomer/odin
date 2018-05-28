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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import com.purplepip.odin.audio.AudioSystemWrapper;
import javax.sound.midi.Instrument;
import org.junit.Before;
import org.junit.Test;

public class SynthesizerDeviceTest {
  private SynthesizerDevice synthesizerDevice;

  /**
   * Set up test.
   */
  @Before
  public void setUp() {
    assumeTrue(new AudioSystemWrapper().isAudioOutputSupported());
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    synthesizerDevice = wrapper.getSynthesizer();
    assumeTrue(wrapper.isOpenSynthesizer());
  }

  @Test
  public void testFindInstrument() {
    Instrument instrument = synthesizerDevice.findInstrumentByName("tubular", false);
    assertNotNull(instrument);
    assertEquals("Cannot find Tubular Bells","Tubular Bells", instrument.getName());
  }

  @Test
  public void testFindDrumkit() {
    Instrument instrument = synthesizerDevice.findInstrumentByName("standard kit", true);
    assertNotNull(instrument);
    assertEquals("Cannot find Standard Kit","Standard Kit", instrument.getName());
  }

  @Test
  public void testFindMissingInstrument() {
    Instrument instrument = synthesizerDevice
        .findInstrumentByName("non-existing-instrument", false);
    assertNull("Cannot find Tubular Bells", instrument);
  }
}
