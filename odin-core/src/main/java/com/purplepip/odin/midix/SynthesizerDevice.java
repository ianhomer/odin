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
import java.util.Locale;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynthesizerDevice extends OdinMidiDevice {
  SynthesizerDevice(@NotNull Synthesizer synthesizer) throws DeviceUnavailableException {
    super(synthesizer);
  }

  /**
   * Find instrument.
   *
   * @param channel        channel
   * @param instrumentName instrument name
   * @return instrument
   * @throws OdinException exception
   */
  public Instrument findInstrument(int channel, String instrumentName) throws OdinException {
    Instrument instrument = findInstrumentByName(instrumentName, channel == 9);
    if (instrument == null) {
      throw new OdinException("Cannot find instrument " + instrumentName);
    }
    LOG.debug("Instrument name {} resolves to {} bank {} program {}", instrumentName,
        instrument.getName(),
        instrument.getPatch().getBank(), instrument.getPatch().getProgram());
    return instrument;
  }

  /**
   * Find an instrument by name.
   *
   * @param name Name of instrument to find
   * @param percussion true if wishing to find percussion instrument
   * @return Instrument instrument
   */
  Instrument findInstrumentByName(String name, boolean percussion) {
    String lowercaseName = name.toLowerCase(Locale.ENGLISH);
    for (Instrument instrument : getMidiDevice().getLoadedInstruments()) {
      if ((!percussion || isPercussion(instrument))
          && instrument.getName().toLowerCase(Locale.ENGLISH).contains(lowercaseName)) {
        return instrument;
      }
    }
    return null;
  }

  @Override
  public Synthesizer getMidiDevice() {
    return (Synthesizer) super.getMidiDevice();
  }

  private static boolean isPercussion(Instrument instrument) {
    /*
     * This is fragile logic based on implementation of SF2Instrument however the
     * com.sun.media.sound.ModelPatch which provides an isPercussion method is not public so
     * any access onto that class is fragile too.
     */
    return instrument.toString().startsWith("Drumkit:");
  }

  /**
   * Log MIDI system instruments available.
   */
  void logInstruments() {
    Instrument[] instruments = getMidiDevice().getLoadedInstruments();
    LOG.debug("Synthesizer info");
    for (Instrument instrument : instruments) {
      LOG.debug("Instruments (loaded) : {}", instrument);
    }
    instruments = getMidiDevice().getAvailableInstruments();
    for (Instrument instrument : instruments) {
      LOG.debug("Instruments (available) : {}", instrument);
    }
    MidiChannel[] midiChannels = getMidiDevice().getChannels();
    for (MidiChannel midiChannel : midiChannels) {
      LOG.debug("Channels : {}", midiChannel.getProgram());
    }
  }
}
