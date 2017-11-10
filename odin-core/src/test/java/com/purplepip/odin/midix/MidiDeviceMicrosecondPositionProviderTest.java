package com.purplepip.odin.midix;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.purplepip.odin.composition.MicrosecondPositionProvider;
import org.junit.Test;

/**
 * MidiDeviceMicrosecondPositionProvider test.
 */
public class MidiDeviceMicrosecondPositionProviderTest {
  @Test
  public void testMicrosecondPosition() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    MicrosecondPositionProvider provider = new MidiDeviceMicrosecondPositionProvider(wrapper);
    assumeTrue(wrapper.getReceivingDevice().isOpen());
    long time = provider.getMicroseconds();
    assertTrue("Microsecond not +ve : " + time, time > -1);
  }
}