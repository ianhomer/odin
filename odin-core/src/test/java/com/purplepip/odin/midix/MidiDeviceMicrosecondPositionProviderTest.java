package com.purplepip.odin.midix;

import static org.junit.Assert.assertTrue;

import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import org.junit.Test;

/**
 * MidiDeviceMicrosecondPositionProvider test.
 */
public class MidiDeviceMicrosecondPositionProviderTest {
  @Test
  public void testMicrosecondPosition() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    MicrosecondPositionProvider provider = new MidiDeviceMicrosecondPositionProvider(wrapper);
    assertTrue(wrapper.getReceivingDevice().isOpen());
    long time = provider.getMicroseconds();
    assertTrue("Microsecond not +ve : " + time, time > -1);
  }
}