package com.purplepip.odin.midix;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * MidiDeviceWrapper test.
 */
public class MidiDeviceWrapperTest {
  @Test
  public void testMidiDeviceWrapper() {
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    assertNotNull("Wrapped device should not be null", wrapper.getDevice());
  }
}
