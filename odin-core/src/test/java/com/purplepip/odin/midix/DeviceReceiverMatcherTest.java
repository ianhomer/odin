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
class DeviceReceiverMatcherTest {
  @Test
  void testMatches() {
    MidiHandle handle = mock(MidiHandle.class);
    when(handle.getName()).thenReturn("Gervill");
    Device device = mock(MidiDevice.class);
    when(device.getHandle()).thenReturn(handle);
    when(device.isSink()).thenReturn(true);
    when(device.getName()).thenCallRealMethod();
    DeviceReceiverMatcher matcher = new DeviceReceiverMatcher("Gervill");
    assertTrue("Device should match", matcher.matches(device));
    when(device.isSink()).thenReturn(false);
    assertFalse("Device should NOT match", matcher.matches(device));
  }
}