package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
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
    try {
      try (MidiDeviceWrapper wrapper = new MidiDeviceWrapper()) {
        assertNotNull("Wrapped device should not be null", wrapper.getReceivingDevice());
      }
    } catch (OdinException e) {
      if (new AudioSystemWrapper().isAudioOutputSupported()) {
        LOG.error("Cannot create MidiDeviceWrapper", e);
        fail("Unexpected exception when initialising MidiDeviceWrapper");
      } else {
        LOG.debug("Expected exception when initialising MidiDeviceWrapper when audio support "
            + " is disabled", e);
      }
    }
  }

  @Test
  public void testChangeProgramByName() throws OdinException {
    assumeTrue(new AudioSystemWrapper().isAudioOutputSupported());
    try (MidiDeviceWrapper wrapper = new MidiDeviceWrapper()) {
      Instrument instrument = wrapper.changeProgram(0,"Bright");
      assertEquals("Instrument should have been changed",
          "Bright Acoustic Pian", instrument.getName());
    }
  }
}
