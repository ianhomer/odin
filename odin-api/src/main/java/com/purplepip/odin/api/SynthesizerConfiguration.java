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

import com.purplepip.odin.boot.OptionalMidiApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Synthesizer configuration. */
@Component
@Profile("!test")
@Order(3)
public class SynthesizerConfiguration implements CommandLineRunner {
  private OptionalMidiApplication midiApplication;

  /**
   * Create synthesizer configuration.
   *
   * @param midiApplication MIDI application
   */
  public SynthesizerConfiguration(@Autowired OptionalMidiApplication midiApplication) {
    this.midiApplication = midiApplication;
  }

  @Override
  public void run(String... args) {
    midiApplication
        .getSynthesizer()
        .ifPresent(
            synthesizer ->
                synthesizer.loadGervillSoundBank("Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2"));
  }
}
