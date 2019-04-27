package com.purplepip.odin.common;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test Odin Exception.
 */
class OdinExceptionTest {
  @Test
  void testOdinExceptionThrow() {
    assertThrows(OdinException.class, () -> {
      throw new OdinException();
    });
  }

  @Test
  void testOdinExceptionWithMessageThrow() {
    assertThrows(OdinException.class, () -> {
      throw new OdinException("test exception");
    });
  }

  @Test
  void testOdinException() {
    assertEquals("test", new OdinException("test").getMessage());
    assertEquals("test", new OdinException("test", new OdinException("internal"))
        .getMessage());
    assertEquals("com.purplepip.odin.common.OdinException: internal",
        new OdinException(new OdinException("internal")).getMessage());
  }
}