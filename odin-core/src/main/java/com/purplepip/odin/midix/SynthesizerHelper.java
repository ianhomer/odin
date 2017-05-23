package com.purplepip.odin.midix;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
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

  public boolean loadGervillSoundBank(String gervillSoundbankFilename) {
    return
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
    } else {
      LOG.info("Synthesizer is null");
    }
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
      if ((!percussion || isPercussion(instrument))
          && instrument.getName().toLowerCase().contains(lowercaseName)) {
        return instrument;
      }
    }
    return null;
  }

  private boolean isPercussion(Instrument instrument) {
    /*
     * This is fragile logic based on implementation of SF2Instrument however the
     * com.sun.media.sound.ModelPatch which provides an isPercussion method is not public so
     * any access onto that class is fragile too.
     */
    return instrument.toString().startsWith("Drumkit:");
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
