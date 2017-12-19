package com.purplepip.odin.clock.tick;

import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.clock.tick.Ticks.HALF;
import static com.purplepip.odin.clock.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.clock.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Wholes;
import org.junit.Test;

/**
 * SameTimeUnitTickConverter test.
 */
public class SameTimeUnitTickConverterTest {
  @Test
  public void testMicrosecondToMillisecond() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> MILLISECOND, () -> MICROSECOND);
    assertEquals(Wholes.valueOf(1000), converter.convert(Wholes.ONE));
  }

  @Test
  public void testBeatToHalf() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> BEAT, () -> HALF);
    assertEquals(Wholes.TWO, converter.convert(Wholes.ONE));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testMicrosecondToBeat() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> MICROSECOND, () -> BEAT);
    converter.convert(Wholes.ONE);
  }

  @Test(expected = OdinRuntimeException.class)
  public void testBeatToMicrosecond() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> BEAT, () -> MICROSECOND);
    converter.convert(Wholes.ONE);
  }

}