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

package com.purplepip.odin.api;

import static com.purplepip.odin.system.Environments.isAudioEnabled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.boot.OptionalMidiApplication;
import com.purplepip.odin.midix.SynthesizerDevice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Synthesizer configuration test. */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class SynthesizerConfigurationTest {
  @Autowired private OptionalMidiApplication midiApplication;

  @Test
  void testRun() {
    assumeTrue(isAudioEnabled());
    SynthesizerConfiguration loader = new SynthesizerConfiguration(midiApplication);
    try (LogCaptor captor = new LogCapture().info().from(SynthesizerDevice.class).start()) {
      loader.run();
      assertEquals(1, captor.size());
    }
  }
}
