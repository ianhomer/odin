package com.purplepip.odin.midix;

import static org.junit.Assert.assertTrue;

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
}
