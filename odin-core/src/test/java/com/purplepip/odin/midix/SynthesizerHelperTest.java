package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.audio.AudioSystemWrapper;
import javax.sound.midi.Instrument;
import org.junit.Before;
import org.junit.Test;

/**
 * Test synthesizer helper.
 */
public class SynthesizerHelperTest {
  private SynthesizerHelper synthesizerHelper;

  /**
   * Set up test.
   */
  @Before
  public void setUp() {
    assumeTrue(new AudioSystemWrapper().isAudioOutputSupported());
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    assumeTrue(wrapper.isOpenSynthesizer());
  }

  @Test
  public void testLogInstruments() {
    try (LogCaptor captor = new LogCapture().debug().from(SynthesizerHelper.class).start()) {
      synthesizerHelper.logInstruments();
      assertTrue("Not enough messages logged", captor.size() > 10);
    }
  }

  @Test
  public void testLoadSoundbank() {
    try (LogCaptor captor = new LogCapture().start()) {
      boolean result = synthesizerHelper.loadGervillSoundBank(
          "soundbank-emg.sf2");
      assertTrue("Cannot load emergency soundbank", result);
      assertEquals(1, captor.size());
    }
  }

  @Test
  public void testLoadMissingSoundbank() {
    try (LogCaptor logCapture = new LogCapture().start()) {
      boolean result = synthesizerHelper.loadGervillSoundBank(
          "soundbank-that-is-missing.sf2");
      assertFalse("Should not be able to load missing soundbank", result);
      assertEquals(1, logCapture.size());
    }
  }

  @Test
  public void testFindInstrument() {
    Instrument instrument = synthesizerHelper.findInstrumentByName("tubular", false);
    assertNotNull(instrument);
    assertEquals("Cannot find Tubular Bells","Tubular Bells", instrument.getName());
  }

  @Test
  public void testFindDrumkit() {
    Instrument instrument = synthesizerHelper.findInstrumentByName("standard kit", true);
    assertNotNull(instrument);
    assertEquals("Cannot find Standard Kit","Standard Kit", instrument.getName());
  }

  @Test
  public void testFindMissingInstrument() {
    Instrument instrument = synthesizerHelper
        .findInstrumentByName("non-existing-instrument", false);
    assertNull("Cannot find Tubular Bells", instrument);
  }
}
