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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.devices.DeviceUnavailableException;
import javax.sound.midi.Instrument;
import javax.sound.midi.Synthesizer;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynthesizerDevice extends OdinMidiDevice {
  private Synthesizer synthesizer;

  SynthesizerDevice(@NotNull Synthesizer synthesizer) throws DeviceUnavailableException {
    super(synthesizer);
    this.synthesizer = synthesizer;
  }

  /**
   * Find instrument.
   *
   * @param channel channel
   * @param instrumentName instrument name
   * @return instrument
   * @throws OdinException exception
   */
  public Instrument findInstrument(int channel, String instrumentName) throws OdinException {
    Instrument instrument = new SynthesizerHelper(synthesizer)
        .findInstrumentByName(instrumentName, channel == 9);
    if (instrument == null) {
      throw new OdinException("Cannot find instrument " + instrumentName);
    }
    LOG.debug("Instrument name {} resolves to {} bank {} program {}", instrumentName,
        instrument.getName(),
        instrument.getPatch().getBank(), instrument.getPatch().getProgram());
    return instrument;
  }
}
