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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.purplepip.odin.audio.AudioSystemWrapper;
import com.purplepip.odin.common.OdinException;
import javax.sound.midi.MidiDevice;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MidiSystemHelperTest {

  @Test
  public void getTransmittingDevice() throws OdinException {
    assumeTrue(new AudioSystemWrapper().isAudioOutputSupported());
    MidiDevice device = new MidiSystemHelper().getTransmittingDevice();
    LOG.debug("Transmitting device : {}", device);
    assertNotNull(device);
  }

  @Test
  public void getReceivingDevice() throws OdinException {
    assumeTrue(new AudioSystemWrapper().isAudioOutputSupported());
    MidiDevice device = new MidiSystemHelper().getReceivingDevice();
    LOG.debug("Receiving device : {}", device);
    assertNotNull(device);
  }

  @Test
  public void testFindMidiDeviceByName() throws OdinException {
    assumeTrue(new AudioSystemWrapper().isAudioOutputSupported());
    MidiDevice device = new MidiSystemHelper().findMidiDeviceByName(
        new MidiDeviceNameStartsWithMatcher("Gervill"));
    LOG.debug("Found device by name : {}", device);
    assertNotNull("Should be able to find device Gervill", device);
  }
}