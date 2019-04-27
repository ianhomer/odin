package com.purplepip.odin.midix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.devices.Device;
import org.junit.jupiter.api.Test;

/**
 * MIDI device in matcher test.
 */
class DeviceTransmitterMatcherTest {
  @Test
  void testMatches() {
    MidiHandle handle = mock(MidiHandle.class);
    when(handle.getName()).thenReturn("Gervill");
    Device device = mock(MidiDevice.class);
    when(device.getHandle()).thenReturn(handle);
    when(device.isSource()).thenReturn(true);
    when(device.getName()).thenCallRealMethod();
    DeviceTransmitterMatcher matcher = new DeviceTransmitterMatcher("Gervill");
    assertTrue("Device should match", matcher.matches(device));
    when(device.isSource()).thenReturn(false);
    assertFalse("Device should NOT match", matcher.matches(device));
  }
}