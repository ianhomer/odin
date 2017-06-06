package com.purplepip.odin.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Odin Runtime exception test.
 */
public class OdinRuntimeExceptionTest {
  @Test(expected = OdinRuntimeException.class)
  public void testOdinRuntimeExceptionThrow() throws OdinRuntimeException {
    throw new OdinRuntimeException("test exception");
  }

  @Test
  public void testOdinRuntimeException() throws OdinException {
    assertEquals("test", new OdinRuntimeException("test").getMessage());
    assertEquals("test", new OdinRuntimeException("test",
        new OdinRuntimeException("internal")).getMessage());
  }
}