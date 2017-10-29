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

package com.purplepip.odin.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.SynthesizerHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Synthesizer configuration test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SynthesizerConfigurationTest {
  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Test
  public void testRun() throws Exception {
    assumeTrue(midiDeviceWrapper.isOpenSynthesizer());
    SynthesizerConfiguration loader = new SynthesizerConfiguration(midiDeviceWrapper);
    try (LogCaptor captor = new LogCapture().info().from(SynthesizerHelper.class).start()) {
      loader.run();
      assertEquals(1, captor.size());
    }
  }
}