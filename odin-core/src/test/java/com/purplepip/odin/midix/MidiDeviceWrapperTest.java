package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.purplepip.odin.common.OdinException;
import javax.sound.midi.Instrument;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * MidiDeviceWrapper test.
 */
@Slf4j
public class MidiDeviceWrapperTest {
  @Test
  public void testMidiDeviceWrapper() {
    try (MidiDeviceWrapper wrapper = new MidiDeviceWrapper()) {
      assertNotNull("Wrapped device should not be null", wrapper.getReceivingDevice());
    }
  }

  @Test
  public void testChangeProgramByName() throws OdinException {
    try (MidiDeviceWrapper wrapper = new MidiDeviceWrapper()) {
      assumeTrue(wrapper.isSynthesizer());
      Instrument instrument = wrapper.changeProgram(0,"Bright");
      assertEquals("Instrument should have been changed",
          "Bright Acoustic Pian", instrument.getName());
    }
  }
}
