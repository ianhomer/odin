package com.purplepip.odin.midix;

import static org.junit.Assert.assertTrue;

import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import org.junit.Test;

/**
 * MidiDeviceMicrosecondPositionProvider test.
 */
public class MidiDeviceMicrosecondPositionProviderTest {
  @Test
  public void getMicrosecondPosition() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    MicrosecondPositionProvider provider = new MidiDeviceMicrosecondPositionProvider(wrapper);
    assertTrue(wrapper.getDevice().isOpen());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    long time = provider.getMicrosecondPosition();
    assertTrue("Microsecond not +ve : " + time, time > 0);
  }
}