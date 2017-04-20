package com.purplepip.odin.series;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Time Unit Converter Test.
 */
public class TickConverterTest {
  private Clock clock;

  @Before
  public void initialise() {
    clock = new Clock(new StaticBeatsPerMinute(120));
  }

  @Test
  public void testConvertToMilliseconds() {
    TickConverter converter = new DefaultTickConverter(clock, Tick.BEAT, Tick.MILLISECOND, 0);
    assertEquals("Beat to ms failed", 500, converter.convert(1));
    converter = new DefaultTickConverter(clock, Tick.HALF, Tick.MILLISECOND, 0);
    assertEquals("Half beat to ms failed", 250, converter.convert(1));
  }

  @Test
  public void testConvertToMicroseconds() {
    TickConverter converter = new DefaultTickConverter(clock, Tick.BEAT, Tick.MICROSECOND, 0);
    assertEquals("Beat to micros failed", 500000, converter.convert(1));
    converter = new DefaultTickConverter(clock, Tick.HALF, Tick.MICROSECOND, 0);
    assertEquals("Half beat to micros failed", 250000, converter.convert(1));
  }

  @Test
  public void testConvertToMicrosecondsWithOffset() {
    TickConverter converter = new DefaultTickConverter(clock, Tick.BEAT, Tick.MICROSECOND, 1);
    assertEquals("Beat to micros failed", 1000000, converter.convert(1));
    converter = new DefaultTickConverter(clock, Tick.HALF, Tick.MICROSECOND, 1);
    assertEquals("Half beat to micros failed", 500000, converter.convert(1));
  }

  @Test
  public void testConvertToBeat() {
    TickConverter converter = new DefaultTickConverter(clock, Tick.MICROSECOND, Tick.BEAT, 0);
    assertEquals("Micros to beat failed", 1, converter.convert(500000));
    converter = new DefaultTickConverter(clock, Tick.MILLISECOND, Tick.BEAT, 0);
    assertEquals("Ms to beat failed", 1, converter.convert(500));
  }

  @Test
  public void testConvertToHalfBeat() {
    TickConverter converter = new DefaultTickConverter(clock, Tick.MICROSECOND, Tick.HALF, 0);
    assertEquals("Micros to half beat failed", 4, converter.convert(1000000));
    converter = new DefaultTickConverter(clock, Tick.MILLISECOND, Tick.HALF, 0);
    assertEquals("Ms to half beat failed", 4, converter.convert(1000));
  }

}