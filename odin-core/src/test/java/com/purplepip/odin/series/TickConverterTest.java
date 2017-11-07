package com.purplepip.odin.series;

import static com.purplepip.odin.sequence.tick.Ticks.BEAT;
import static com.purplepip.odin.sequence.tick.Ticks.HALF;
import static com.purplepip.odin.sequence.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.sequence.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.clock.BeatClock;
import com.purplepip.odin.sequence.tick.DefaultTickConverter;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.TickConverter;
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
      Real forward;
      Real back;
      Real forwardExpect;
      Real backExpect;

      if (duration) {
        forward = converter.convertDuration(Whole.valueOf(times[i]), Whole.valueOf(times[i + 1]));
        forwardExpect = Whole.valueOf(times[i + 2]);
        back = converter.convertDurationBack(Whole.valueOf(times[i]), Whole.valueOf(times[i + 2]));
        backExpect = Whole.valueOf(times[i + 1]);
      } else {
        forward = converter.convert(Whole.valueOf(times[i]));
        forwardExpect = Whole.valueOf(times[i + 1]);
        back = converter.convertBack(Whole.valueOf(times[i + 1]));
        backExpect = Whole.valueOf(times[i]);
      }

      assertEquals(label + sourceTick + " to " + targetTick + " failed",
          forwardExpect, forward);
      assertEquals(label + targetTick + " back to " + sourceTick + " failed",
          backExpect, back);
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