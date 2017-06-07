package com.purplepip.odin.midi;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import org.junit.Test;

/**
 * Raw message test.
 */
public class RawMessageTest {
  @Test
  public void testProgramChange() throws OdinException {
    RawMessage message = new RawMessage(
        new ProgramChangeOperation(0,1,2));
    assertEquals(3, message.getBytes().length);
    assertEquals(-64, message.getBytes()[0]);
    assertEquals(2, message.getBytes()[1]);
    assertEquals(0, message.getBytes()[2]);
  }
}