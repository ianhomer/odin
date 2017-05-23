package com.purplepip.odin.midix;

import static org.junit.Assert.assertNotNull;

import com.purplepip.odin.common.OdinException;
import org.junit.Test;

/**
 * MidiDeviceWrapper test.
 */
public class MidiDeviceWrapperTest {
  @Test
  public void testMidiDeviceWrapper() {
    try (MidiDeviceWrapper wrapper = new MidiDeviceWrapper()) {
      assertNotNull("Wrapped device should not be null", wrapper.getDevice());
    }
  }

  @Test
  public void testChangeProgramByName() throws OdinException {
    try (MidiDeviceWrapper wrapper = new MidiDeviceWrapper()) {
      wrapper.changeProgram(0,"Bright");
    }
  }
}
