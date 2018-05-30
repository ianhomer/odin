package com.purplepip.odin.midix;

import static com.purplepip.odin.configuration.Environments.newAudioEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
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
    assumeTrue(!newAudioEnvironment().isEmpty());
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    synthesizerHelper = new SynthesizerHelper(wrapper.getSynthesizer());
    assumeTrue(wrapper.isOpenSynthesizer());
  }

  @Test
  public void testLogInstruments() {
    try (LogCaptor captor = new LogCapture().debug().from(SynthesizerDevice.class).start()) {
      new MidiDeviceWrapper().getSynthesizer().logInstruments();
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
}
