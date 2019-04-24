package com.purplepip.odin.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test Odin Exception.
 */
public class OdinExceptionTest {
  @Test(expected = OdinException.class)
  public void testOdinExceptionThrow() throws OdinException {
    throw new OdinException();
  }

  @Test(expected = OdinException.class)
  public void testOdinExceptionWithMessageThrow() throws OdinException {
    throw new OdinException("test exception");
  }

  @Test
  public void testOdinException() throws OdinException {
    assertEquals("test", new OdinException("test").getMessage());
    assertEquals("test", new OdinException("test", new OdinException("internal"))
        .getMessage());
    assertEquals("com.purplepip.odin.common.OdinException: internal",
        new OdinException(new OdinException("internal")).getMessage());
  }
}