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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeTrue;

import com.purplepip.odin.audio.AudioHandle;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.devices.Handle;
import com.purplepip.odin.midix.MidiHandle;
import com.purplepip.odin.midix.SynthesizerDevice;
import java.util.Optional;
import javax.sound.sampled.AudioSystem;
import org.junit.Test;

public class EnvironmentsTest {

  @Test
  public void testAudioEnvironmentWithAudioDisabled() {
    Environment environment = Environments.newAudioEnvironment(() -> false);
    assertTrue(environment.isEmpty());
  }

  @Test
  public void testAudioEnvironmentWithAudioEnabled() {
    assumeTrue(AudioSystem.getMixerInfo().length > 0);
    Environment environment = Environments.newAudioEnvironment(() -> true);
    assertFalse(environment.isEmpty());
  }

  @Test
  public void testEnvironmentWithAudioDisabled() {
    Environment environment = Environments.newEnvironment(() -> false);
    assertTrue(environment.noneMatch(AudioHandle.class));
  }

  @Test
  public void testEnvironmentWithAudioEnabled() throws DeviceUnavailableException {
    assumeTrue(AudioSystem.getMixerInfo().length > 0);
    Environment environment = Environments.newEnvironment(() -> true);
    assertFalse(environment.noneMatch(AudioHandle.class));
    Optional<Handle> sink = environment.findOneSink(MidiHandle.class);
    assertTrue("Sink should be present", sink.isPresent());
    assertTrue(sink.get().open() instanceof SynthesizerDevice);
  }
}