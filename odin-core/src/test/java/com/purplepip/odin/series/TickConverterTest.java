package com.purplepip.odin.series;

import static com.purplepip.odin.sequence.tick.Ticks.BEAT;
import static com.purplepip.odin.sequence.tick.Ticks.HALF;
import static com.purplepip.odin.sequence.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.sequence.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.tick.Tick;
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

  private void assertConversion(BeatClock clock, Tick sourceTick, Tick targetTick,
                                long offset, boolean duration, long... times) {
    TickConverter converter = new DefaultTickConverter(
        clock, () -> sourceTick, () -> targetTick, () -> offset);
    String label = (duration ? "duration" : "time") + " : ";
    int increment = (duration ? 3 : 2);
    for (int i = 0 ; i < times.length ; i = i + increment) {
      double forward;
      double back;
      double forwardExpect;
      double backExpect;

      if (duration) {
        forward = converter.convertDuration(times[i], times[i + 1]);
        forwardExpect = times[i + 2];
        back = converter.convertDurationBack(times[i], times[i + 2]);
        backExpect = times[i + 1];
      } else {
        forward = converter.convert(times[i]);
        forwardExpect = times[i + 1];
        back = converter.convertBack(times[i + 1]);
        backExpect = times[i];
      }

      assertEquals(label + sourceTick + " to " + targetTick + " failed",
          forwardExpect, forward, 0.0001);
      assertEquals(label + targetTick + " back to " + sourceTick + " failed",
          backExpect, back, 0.0001);
    }
  }

  @Test
  public void testConvertToMilliseconds() {
    assertConversion(clock, BEAT, MILLISECOND, 0, false, 1,500);
    assertConversion(clock, BEAT, MILLISECOND, 0, true, 100,1,500);
  }

  @Test
  public void testConvertHalfBeatToMilliseconds() {
    assertConversion(clock, HALF, MILLISECOND, 0, false, 1,250);
  }

  @Test
  public void testConvertToMicroseconds() {
    assertConversion(clock, BEAT, MICROSECOND, 0,false, 1,500000);
    assertConversion(clock, HALF, MICROSECOND, 0,false, 1,250000);
  }

  @Test
  public void testConvertToMicrosecondsWithOffset() {
    assertConversion(clock, BEAT, MICROSECOND, 1, false, 1,1000000);
    assertConversion(clock, HALF, MICROSECOND, 1, false, 1, 500000);
  }

  @Test
  public void testConvertToBeat() {
    assertConversion(clock, MICROSECOND, BEAT, 0, false, 500000, 1);
    assertConversion(clock, MILLISECOND, BEAT, 0, false, 500, 1);
  }

  @Test
  public void testConvertToHalfBeat() {
    assertConversion(clock, MICROSECOND, HALF, 0, false, 1000000, 4);
    assertConversion(clock, MILLISECOND, HALF, 0, false, 1000, 4);
  }
}