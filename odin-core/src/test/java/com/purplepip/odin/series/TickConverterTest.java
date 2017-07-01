package com.purplepip.odin.series;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.tick.RuntimeTicks;
import org.junit.Before;
import org.junit.Test;

/**
 * Time Unit Converter Test.
 */
public class TickConverterTest {
  private BeatClock clock;

  @Before
  public void initialise() {
    clock = new BeatClock(new StaticBeatsPerMinute(120));
  }

  @Test
  public void testConvertToMilliseconds() {
    TickConverter converter = new DefaultTickConverter(clock, RuntimeTicks.BEAT,
        RuntimeTicks.MILLISECOND, () -> 0);
    assertEquals("Beat to ms failed", 500, converter.convert(1));
    converter = new DefaultTickConverter(clock, RuntimeTicks.HALF,
        RuntimeTicks.MILLISECOND, () -> 0);
    assertEquals("Half beat to ms failed", 250, converter.convert(1));
  }

  @Test
  public void testConvertToMicroseconds() {
    TickConverter converter = new DefaultTickConverter(clock, RuntimeTicks.BEAT,
        RuntimeTicks.MICROSECOND, () -> 0);
    assertEquals("Beat to micros failed", 500000, converter.convert(1));
    converter = new DefaultTickConverter(clock, RuntimeTicks.HALF,
        RuntimeTicks.MICROSECOND, () -> 0);
    assertEquals("Half beat to micros failed", 250000, converter.convert(1));
  }

  @Test
  public void testConvertToMicrosecondsWithOffset() {
    TickConverter converter = new DefaultTickConverter(clock, RuntimeTicks.BEAT,
        RuntimeTicks.MICROSECOND, () -> 1);
    assertEquals("Beat to micros failed", 1000000, converter.convert(1));
    converter = new DefaultTickConverter(clock, RuntimeTicks.HALF,
        RuntimeTicks.MICROSECOND, () -> 1);
    assertEquals("Half beat to micros failed", 500000, converter.convert(1));
  }

  @Test
  public void testConvertToBeat() {
    TickConverter converter = new DefaultTickConverter(clock, RuntimeTicks.MICROSECOND,
        RuntimeTicks.BEAT, () -> 0);
    assertEquals("Micros to beat failed", 1, converter.convert(500000));
    converter = new DefaultTickConverter(clock, RuntimeTicks.MILLISECOND,
        RuntimeTicks.BEAT, () -> 0);
    assertEquals("Ms to beat failed", 1, converter.convert(500));
  }

  @Test
  public void testConvertToHalfBeat() {
    TickConverter converter = new DefaultTickConverter(clock, RuntimeTicks.MICROSECOND,
        RuntimeTicks.HALF, () -> 0);
    assertEquals("Micros to half beat failed", 4, converter.convert(1000000));
    converter = new DefaultTickConverter(clock, RuntimeTicks.MILLISECOND,
        RuntimeTicks.HALF, () -> 0);
    assertEquals("Ms to half beat failed", 4, converter.convert(1000));
  }

}