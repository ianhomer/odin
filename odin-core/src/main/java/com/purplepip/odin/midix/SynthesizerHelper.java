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

  public void loadGervillSoundBank(String gervillSoundbankFilename) {
    loadSoundBank(System.getProperty("user.home") + "/.gervill/" + gervillSoundbankFilename);
  }

  private Synthesizer ensureOpen(Synthesizer synthesizer) throws MidiUnavailableException {
    if (!synthesizer.isOpen()) {
      synthesizer.open();
    }
    return synthesizer;
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
    Synthesizer synthesizer;
    try {
      synthesizer = ensureOpen(MidiSystem.getSynthesizer());
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get opened synthesizer", e);
      return false;
    }
    //synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
    Soundbank soundbank;
    try {
      soundbank = MidiSystem.getSoundbank(file);
    } catch (InvalidMidiDataException | IOException e) {
      LOG.error("Cannot get soundbank", e);
      return false;
    }
    logSoundbank(soundbank);
    boolean result = synthesizer.loadAllInstruments(soundbank);
    LOG.info("Loaded soundbank {} : {}", soundbank.getName(), result);

    return result;
  }

  /**
   * Log MIDI system instruments available.
   */
  public void logInstruments() {
    Synthesizer synthesizer;
    try {
      synthesizer = MidiSystem.getSynthesizer();
      if (synthesizer != null) {
        Instrument[] instruments = synthesizer.getLoadedInstruments();
        for (int i = 0; i < instruments.length; i++) {
          LOG.debug("Synthesiser instruments (loaded) : {} {} {}",
              instruments[i].getPatch().getBank(),
              instruments[i].getPatch().getProgram(), instruments[i].getName());
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
      }
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get synthesizer", e);
    }
  }

  /**
   * Change program (via the Synthesizer API).
   *
   * @param channel channel on which to change the program
   * @param program program to change to
   */
  public void changeProgram(int channel, int program) {
    try {
      Synthesizer synthesizer = MidiSystem.getSynthesizer();
      synthesizer.getChannels()[channel].programChange(program);
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot change program");
    }
    LOG.info("Changed channel {} to program {} (via Synthesizer API)", channel, program);
  }


  /**
   * Find an instrument by name.
   *
   * @param name Name of instrument to find
   * @return Instrument
   * @throws MidiUnavailableException Exception
   */
  public Instrument findInstrumentByName(String name) throws MidiUnavailableException {
    for (Instrument instrument : MidiSystem.getSynthesizer().getAvailableInstruments()) {
      if (instrument.getName().equals(name)) {
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
