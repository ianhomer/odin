package com.purplepip.odin.midix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * MIDI device in matcher test.
 */
@RunWith(MockitoJUnitRunner.class)
public class MidiDeviceInMatcherTest {
  @Mock
  private MidiDevice device;

  @Test
  public void matches() throws Exception {
    MidiDeviceInMatcher matcher = new MidiDeviceInMatcher("Gervill");
    MidiDevice synthesizer = MidiSystem.getSynthesizer();
    when(device.getDeviceInfo()).thenReturn(synthesizer.getDeviceInfo());
    when(device.getMaxTransmitters()).thenReturn(1);
    assertTrue("Device should match", matcher.matches(device));
    when(device.getMaxTransmitters()).thenReturn(-1);
    assertFalse("Device should NOT match", matcher.matches(device));
  }

}