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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.devices.DeviceUnavailableException;
import javax.sound.midi.Instrument;
import javax.sound.midi.Synthesizer;
import org.junit.Test;

/**
 * Coverage of synthesizer helper that can be done when audio output is not supported.  This
 * is required to provide test coverage on build machines that do not support audio output and
 * also is more strictly a unit test.
 */
public class NoAudioSynthesizerHelperTest {
  @Test
  public void testHelper() throws DeviceUnavailableException {
    Instrument instrument = mock(Instrument.class);
    when(instrument.getName()).thenReturn("mock-instrument");
    Synthesizer synthesizer = mock(Synthesizer.class);
    Instrument[] instruments = new Instrument[] { instrument };
    when(synthesizer.getLoadedInstruments()).thenReturn(instruments);
    SynthesizerHelper helper = new SynthesizerHelper(new SynthesizerDevice(synthesizer));
    Instrument foundInstrument =
        helper.findInstrumentByName("mock-instrument", false);
    assertEquals("mock-instrument", foundInstrument.getName());
  }
}
