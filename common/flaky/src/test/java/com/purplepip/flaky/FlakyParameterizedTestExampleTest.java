package com.purplepip.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FlakyParameterizedTestExampleTest {
  @ParameterizedTest
  @ValueSource(ints = {2})
  void shouldBeOk(Integer value) {
    assertEquals(2, value.intValue());
  }
}
