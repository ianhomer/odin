package com.purplepip.odin.sequence;

import static com.purplepip.odin.sequence.tick.Ticks.BEAT;
import static com.purplepip.odin.sequence.tick.Ticks.HALF;
import static com.purplepip.odin.sequence.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.sequence.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinRuntimeException;
import org.junit.Test;

/**
 * SameTimeUnitTickConverter test.
 */
public class SameTimeUnitTickConverterTest {
  @Test
  public void testMicrosecondToMillisecond() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> MILLISECOND, () -> MICROSECOND);
    assertEquals(1000, converter.convert(1),0.001);
  }

  @Test
  public void testBeatToHalf() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> BEAT, () -> HALF);
    assertEquals(2, converter.convert(1), 0.001);
  }

  @Test(expected = OdinRuntimeException.class)
  public void testMicrosecondToBeat() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> MICROSECOND, () -> BEAT);
    converter.convert(1);
  }

  @Test(expected = OdinRuntimeException.class)
  public void testBeatToMicrosecond() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        () -> BEAT, () -> MICROSECOND);
    converter.convert(1);
  }

}