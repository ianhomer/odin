/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.SynthesizerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class SynthesizerConfiguration implements ApplicationRunner {
  private MidiDeviceWrapper midiDeviceWrapper;

  public SynthesizerConfiguration(@Autowired MidiDeviceWrapper midiDeviceWrapper) {
    this.midiDeviceWrapper = midiDeviceWrapper;
  }

  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {
    if (midiDeviceWrapper.isSynthesizer()) {
      new SynthesizerHelper(midiDeviceWrapper.getSynthesizer())
          .loadGervillSoundBank(
              "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
    }
  }
}
