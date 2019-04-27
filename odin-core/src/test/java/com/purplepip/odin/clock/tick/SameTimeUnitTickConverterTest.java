package com.purplepip.odin.clock.tick;

import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.clock.tick.Ticks.HALF;
import static com.purplepip.odin.clock.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.clock.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Wholes;
import org.junit.jupiter.api.Test;

/**
 * SameTimeUnitTickConverter test.
 */
class SameTimeUnitTickConverterTest {
  @Test
  void testMicrosecondToMillisecond() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> MILLISECOND, () -> MICROSECOND);
    assertEquals(Wholes.valueOf(1000), converter.convert(Wholes.ONE));
  }

  @Test
  void testBeatToHalf() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> BEAT, () -> HALF);
    assertEquals(Wholes.TWO, converter.convert(Wholes.ONE));
  }

  @Test
  void testMicrosecondToBeat() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> MICROSECOND, () -> BEAT);
    assertThrows(OdinRuntimeException.class, () ->
        converter.convert(Wholes.ONE)
    );
  }

  @Test
  void testBeatToMicrosecond() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> BEAT, () -> MICROSECOND);
    assertThrows(OdinRuntimeException.class, () ->
        converter.convert(Wholes.ONE)
    );
  }

}