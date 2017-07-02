package com.purplepip.odin.series;

import static com.purplepip.odin.sequence.tick.RuntimeTicks.BEAT;
import static com.purplepip.odin.sequence.tick.RuntimeTicks.HALF;
import static com.purplepip.odin.sequence.tick.RuntimeTicks.MICROSECOND;
import static com.purplepip.odin.sequence.tick.RuntimeTicks.MILLISECOND;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.tick.RuntimeTick;
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

  private void assertConversion(BeatClock clock, RuntimeTick sourceTick, RuntimeTick targetTick,
                                long offset, long... times) {
    TickConverter converter = new DefaultTickConverter(
        clock, () -> sourceTick, () -> targetTick, () -> offset);
    for (int i = 0 ; i < times.length ; i = i + 2) {
      assertEquals(sourceTick + " to " + targetTick + " failed",
          times[i + 1], converter.convert(times[i]));
      assertEquals(targetTick + " back to " + sourceTick + " failed",
          times[i], converter.convertBack(times[i + 1]));
    }
  }

  @Test
  public void testConvertToMilliseconds() {
    assertConversion(clock, BEAT, MILLISECOND, 0, 1,500);
  }

  @Test
  public void testConvertHalfBeatToMilliseconds() {
    assertConversion(clock, HALF, MILLISECOND, 0, 1,250);
  }

  @Test
  public void testConvertToMicroseconds() {
    assertConversion(clock, BEAT, MICROSECOND, 0, 1,500000);
    assertConversion(clock, HALF, MICROSECOND, 0, 1,250000);
  }

  @Test
  public void testConvertToMicrosecondsWithOffset() {
    assertConversion(clock, BEAT, MICROSECOND, 1, 1,1000000);
    assertConversion(clock, HALF, MICROSECOND, 1, 1,500000);
  }

  @Test
  public void testConvertToBeat() {
    assertConversion(clock, MICROSECOND, BEAT, 0, 500000, 1);
    assertConversion(clock, MILLISECOND, BEAT, 0, 500, 1);
  }

  @Test
  public void testConvertToHalfBeat() {
    assertConversion(clock, MICROSECOND, HALF, 0, 1000000, 4);
    assertConversion(clock, MILLISECOND, HALF, 0, 1000, 4);
  }
}