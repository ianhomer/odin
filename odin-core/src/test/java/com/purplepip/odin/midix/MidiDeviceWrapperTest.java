package com.purplepip.odin.midix;

import static com.purplepip.odin.system.Environments.newAudioEnvironment;
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
      // TODO : Need to make MidiDeviceWrapper throw exception on not found
      if (!newAudioEnvironment().isEmpty()) {
        assertNotNull("Wrapped receiving device should not be null",
            wrapper.getReceivingDevice());
      }
    }
  }

  @Test
  public void testDefaultToGervill() {
    assumeTrue(!newAudioEnvironment().isEmpty());
    MidiDeviceWrapper wrapper = new MidiDeviceWrapper();
    assertNotNull(wrapper.getReceivingDevice());
  }

  @Test
  public void testChangeProgramByName() throws OdinException {
    assumeTrue(!newAudioEnvironment().isEmpty());
    try (MidiDeviceWrapper wrapper = new MidiDeviceWrapper()) {
      Instrument instrument = wrapper.getSynthesizer()
          .changeProgram(0,"Bright");
      assertEquals("Instrument should have been changed",
          "Bright Acoustic Pian", instrument.getName());
    }
  }
}
