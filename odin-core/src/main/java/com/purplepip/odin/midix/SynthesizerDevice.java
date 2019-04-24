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

import static com.purplepip.odin.system.Environments.newAudioEnvironment;

import com.purplepip.odin.audio.AudioSystemWrapper;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.midi.RawMessage;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynthesizerDevice extends MidiDevice implements SynthesizerReceiver {
  private final Synthesizer synthesizer;

  SynthesizerDevice(@NotNull MidiHandle handle, @NotNull Synthesizer synthesizer)
      throws DeviceUnavailableException {
    super(handle, synthesizer);
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
  @Override
  public Instrument findInstrument(int channel, String instrumentName) throws OdinException {
    Instrument instrument = findInstrumentByName(instrumentName, channel == 9);
    if (instrument == null) {
      throw new OdinException("Cannot find instrument " + instrumentName);
    }
    LOG.debug(
        "Instrument name {} resolves to {} bank {} program {}",
        instrumentName,
        instrument.getName(),
        instrument.getPatch().getBank(),
        instrument.getPatch().getProgram());
    return instrument;
  }

  /**
   * Change channel to first instrument found that contains the given instrument name string.
   *
   * @param channel channel to change
   * @param instrumentName instrument name to search for
   * @throws OdinException exception
   */
  Instrument changeProgram(int channel, String instrumentName) throws OdinException {
    Instrument instrument = findInstrument(channel, instrumentName);
    changeProgram(channel, instrument.getPatch().getBank(), instrument.getPatch().getProgram());
    return instrument;
  }

  /**
   * Change program via a MIDI program change message.
   *
   * @param channel channel to change
   * @param bank bank to set
   * @param program program to set
   */
  private void changeProgram(int channel, int bank, int program) {
    try {
      synthesizer
          .getReceiver()
          .send(
              new RawMidiMessage(
                  new RawMessage(new ProgramChangeOperation(channel, bank, program)).getBytes()),
              -1);
    } catch (MidiUnavailableException | OdinException e) {
      LOG.error("Cannot change synthesizer instruments", e);
    }
    LOG.debug("Changed channel {} to program {}", channel, program);
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
    for (Instrument instrument : synthesizer.getLoadedInstruments()) {
      if ((!percussion || isPercussion(instrument))
          && instrument.getName().toLowerCase(Locale.ENGLISH).contains(lowercaseName)) {
        return instrument;
      }
    }
    return null;
  }

  public Instrument[] getLoadedInstruments() {
    return synthesizer.getLoadedInstruments();
  }

  private static boolean isPercussion(Instrument instrument) {
    /*
     * This is fragile logic based on implementation of SF2Instrument however the
     * com.sun.media.sound.ModelPatch which provides an isPercussion method is not public so
     * any access onto that class is fragile too.
     */
    return instrument.toString().startsWith("Drumkit:");
  }

  /** Log MIDI system instruments available. */
  void logInstruments() {
    Instrument[] instruments = synthesizer.getLoadedInstruments();
    LOG.debug("Synthesizer info");
    for (Instrument instrument : instruments) {
      LOG.debug("Instruments (loaded) : {}", instrument);
    }
    instruments = synthesizer.getAvailableInstruments();
    for (Instrument instrument : instruments) {
      LOG.debug("Instruments (available) : {}", instrument);
    }
    MidiChannel[] midiChannels = synthesizer.getChannels();
    for (MidiChannel midiChannel : midiChannels) {
      LOG.debug("Channels : {}", midiChannel.getProgram());
    }
  }

  protected void open() throws DeviceUnavailableException {
    if (newAudioEnvironment().isEmpty()) {
      LOG.warn(
          "Cannot open synthesizer device {} when no audio handles are available", getHandle());
      LOG.debug(
          "Stack for call that opened synthesizer", new Exception("Stack opening synthesizer"));
      new AudioSystemWrapper().dump(true);
    } else {
      super.open();
    }
  }

  public boolean loadGervillSoundBank(String gervillSoundbankFilename) {
    return loadSoundBank(System.getProperty("user.home") + "/.gervill/" + gervillSoundbankFilename);
  }

  /**
   * Load sound bank.
   *
   * @param pathname path location for the soundbank file
   * @return true if sound bank loaded OK
   */
  private boolean loadSoundBank(String pathname) {
    File file = new File(pathname);
    if (!file.exists()) {
      LOG.info("Cannot find file {} to load soundbank from", pathname);
      return false;
    }
    boolean isOpenResult = isOpen();
    assert isOpenResult;
    synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
    Soundbank soundbank;
    try {
      soundbank = MidiSystem.getSoundbank(file);
    } catch (InvalidMidiDataException | IOException e) {
      LOG.error("Cannot get soundbank", e);
      return false;
    }
    synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
    boolean result = synthesizer.loadAllInstruments(soundbank);
    LOG.info("Loaded soundbank {} : {}", soundbank.getName(), result);

    return result;
  }
}
