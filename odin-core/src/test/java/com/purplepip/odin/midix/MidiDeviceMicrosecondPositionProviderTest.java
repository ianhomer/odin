package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.common.OdinException;
import javax.sound.midi.MidiDevice;
import org.junit.Test;

/**
 * MidiDeviceMicrosecondPositionProvider test.
 */
public class MidiDeviceMicrosecondPositionProviderTest {
  @Test
  public void testMockedMicrosecondPosition() {
    MidiDevice device = mock(MidiDevice.class);
    when(device.getMicrosecondPosition()).thenReturn(10L);
    assertEquals(10, new MidiDeviceMicrosecondPositionProvider(device).getMicroseconds());
  }

  @Test
  public void testMicrosecondPosition() throws OdinException {
    assumeTrue(new AudioSystemWrapper().isAudioOutputSupported());
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    MicrosecondPositionProvider provider = new MidiDeviceMicrosecondPositionProvider(
        wrapper.getReceivingDevice());
    long time = provider.getMicroseconds();
    assertTrue("Microsecond not +ve : " + time, time > -1);
  }
}