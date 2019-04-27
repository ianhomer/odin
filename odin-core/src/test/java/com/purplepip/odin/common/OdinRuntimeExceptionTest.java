package com.purplepip.odin.common;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Odin Runtime exception test.
 */
class OdinRuntimeExceptionTest {
  @Test
  void testOdinRuntimeExceptionThrow() {
    assertThrows(OdinRuntimeException.class, () -> {
      throw new OdinRuntimeException("test exception");
    });
  }

  @Test
  void testOdinRuntimeException() {
    assertEquals("test", new OdinRuntimeException("test").getMessage());
    assertEquals("test", new OdinRuntimeException("test",
        new OdinRuntimeException("internal")).getMessage());
  }
}