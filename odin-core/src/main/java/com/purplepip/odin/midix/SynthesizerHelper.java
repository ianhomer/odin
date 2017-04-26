package com.purplepip.odin.midix;

import com.sun.media.sound.ModelPatch;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Synthesizer Helper.
 */
public class SynthesizerHelper {
  private static final Logger LOG = LoggerFactory.getLogger(SynthesizerHelper.class);

  private Synthesizer synthesizer;

  public SynthesizerHelper(Synthesizer synthesizer) {
    this.synthesizer = synthesizer;
  }

  public void loadGervillSoundBank(String gervillSoundbankFilename) {
    loadSoundBank(System.getProperty("user.home") + "/.gervill/" + gervillSoundbankFilename);
  }

  private void ensureOpen() throws MidiUnavailableException {
    if (!synthesizer.isOpen()) {
      synthesizer.open();
    }
  }

  /**
   * Load sound bank.
   *
   * @param pathname path location for the soundbank file
   * @return true if sound bank loaded OK
   */
  public boolean loadSoundBank(String pathname) {
    File file = new File(pathname);
    if (!file.exists()) {
      LOG.info("Cannot find file {} to load soundbank from", pathname);
      return false;
    }
    try {
      ensureOpen();
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get opened synthesizer", e);
      return false;
    }
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

  /**
   * Log MIDI system instruments available.
   */
  public void logInstruments() {
    if (synthesizer != null) {
      Instrument[] instruments = synthesizer.getLoadedInstruments();
      for (int i = 0; i < instruments.length; i++) {
        Instrument instrument = instruments[i];
        Patch patch = instrument.getPatch();
        Boolean isPercussion = isPercussion(instrument);

        LOG.debug("Synthesiser instruments (loaded) :{} {} {} {}",
            Boolean.TRUE.equals(isPercussion) ? "(percussion)" : "",
            patch.getBank(),
            patch.getProgram(), instrument.getName());
      }
      instruments = synthesizer.getAvailableInstruments();
      for (int i = 0; i < instruments.length; i++) {
        LOG.debug("Synthesiser instruments (available) : {} {} {}",
            instruments[i].getPatch().getBank(),
            instruments[i].getPatch().getProgram(), instruments[i].getName());
      }
      MidiChannel[] midiChannels = synthesizer.getChannels();
      for (int i = 0; i < midiChannels.length; i++) {
        LOG.debug("Synthesiser channels : {}", midiChannels[i].getProgram());
      }
    } else {
      LOG.info("Synthesizer is null");
    }
  }

  private Boolean isPercussion(Instrument instrument) {
    Patch patch = instrument.getPatch();
    if (patch instanceof ModelPatch) {
      ModelPatch modelPatch = (ModelPatch) patch;
      return modelPatch.isPercussion();
    }
    return null;
  }

  /**
   * Find an instrument by name.
   *
   * @param name Name of instrument to find
   * @return Instrument
   */
  public Instrument findInstrumentByName(String name, boolean percussion) {
    String lowercaseName = name.toLowerCase();
    for (Instrument instrument : synthesizer.getLoadedInstruments()) {
      if ((!percussion || Boolean.TRUE.equals(isPercussion(instrument))) &&
          instrument.getName().toLowerCase().contains(lowercaseName)) {
        return instrument;
      }
    }
    return null;
  }

  /**
   * Log information about soundbank.
   *
   * @param soundbank soundbank to log information about.
   */
  public void logSoundbank(Soundbank soundbank) {
    LOG.info("Soundbank {}", soundbank.getName());
    for (Instrument instrument : soundbank.getInstruments()) {
      LOG.debug("Instrument {} {} {}", instrument.getPatch().getBank(),
          instrument.getPatch().getProgram(),
          instrument.getName());
    }
  }

}
