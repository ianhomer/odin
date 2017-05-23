package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.sound.midi.Instrument;

import org.junit.Test;


/**
 * Test synthesizer helper.
 */
public class SynthesizerHelperTest {
  @Test
  public void testLogInstruments() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    SynthesizerHelper synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    synthesizerHelper.logInstruments();
  }

  @Test
  public void testLoadSoundbank() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    SynthesizerHelper synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    boolean result = synthesizerHelper.loadGervillSoundBank(
        "soundbank-emg.sf2");
    assertTrue("Cannot load emergency soundbank", result);
  }

  @Test
  public void testLoadMissingSoundbank() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    SynthesizerHelper synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    boolean result = synthesizerHelper.loadGervillSoundBank(
        "soundbank-that-is-missing.sf2");
    assertFalse("Should not be able to load missing soundbank", result);
  }

  @Test
  public void testFindInstrument() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    SynthesizerHelper synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    Instrument instrument = synthesizerHelper.findInstrumentByName("tubular", false);
    assertEquals("Cannot find Tubular Bells",
        "Tubular Bells", instrument.getName());
  }

  @Test
  public void testFindDrumkit() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    SynthesizerHelper synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    Instrument instrument = synthesizerHelper.findInstrumentByName("standard kit", true);
    assertEquals("Cannot find Standard Kit",
        "Standard Kit", instrument.getName());
  }

  @Test
  public void testFindMissingInstrument() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    SynthesizerHelper synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    Instrument instrument = synthesizerHelper
        .findInstrumentByName("non-existing-instrument", false);
    assertNull("Cannot find Tubular Bells", instrument);
  }

}
