package com.purplepip.odin.sequence;

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
        RuntimeTicks.MILLISECOND, RuntimeTicks.MICROSECOND);
    assertEquals(1000, converter.convert(1));
  }

  @Test
  public void testBeatToHalf() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        RuntimeTicks.BEAT, RuntimeTicks.HALF);
    assertEquals(2, converter.convert(1));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testMicrosecondToBeat() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        RuntimeTicks.MICROSECOND, RuntimeTicks.BEAT);
    converter.convert(1);
  }

  @Test(expected = OdinRuntimeException.class)
  public void testBeatToMicrosecond() {
    SameTimeUnitTickConverter converter = new SameTimeUnitTickConverter(
        RuntimeTicks.BEAT, RuntimeTicks.MICROSECOND);
    converter.convert(1);
  }

}