package com.purplepip.odin.midix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.devices.Device;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Transmitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * MIDI device in matcher test.
 */
@RunWith(MockitoJUnitRunner.class)
public class MidiDeviceTransmitterMatcherTest {
  @Mock
  private MidiDevice midiDevice;

  @Test
  public void testMatches() throws Exception {
    MidiDevice synthesizer = MidiSystem.getSynthesizer();
    when(midiDevice.getDeviceInfo()).thenReturn(synthesizer.getDeviceInfo());
    when(midiDevice.getMaxTransmitters()).thenReturn(1);
    when(midiDevice.getTransmitter()).thenReturn(mock(Transmitter.class));
    MidiHandle handle = mock(MidiHandle.class);
    when(handle.getName()).thenReturn("Gervill");
    Device device = new OdinMidiDevice(handle, midiDevice);
    MidiDeviceTransmitterMatcher matcher = new MidiDeviceTransmitterMatcher("Gervill");
    assertTrue("Device should match", matcher.matches(device));
    when(midiDevice.getMaxTransmitters()).thenReturn(-1);
    assertTrue("Device should match", matcher.matches(device));
    when(midiDevice.getMaxTransmitters()).thenReturn(0);
    assertFalse("Device should NOT match", matcher.matches(device));
  }
}