package com.purplepip.odin.series;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.clock.tick.Ticks.HALF;
import static com.purplepip.odin.clock.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.clock.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Time Unit Converter Test. */
class TickConverterTest {
  private BeatClock clock;

  @BeforeEach
  void initialise() {
    clock = newPrecisionBeatClock(120);
  }

  private void assertConversion(
      BeatClock clock,
      Tick sourceTick,
      Tick targetTick,
      Rational offset,
      boolean duration,
      long... times) {
    TickConverter converter =
        new DefaultTickConverter(clock, () -> sourceTick, () -> targetTick, () -> offset);
    String label = (duration ? "duration" : "time") + " : ";
    int increment = (duration ? 3 : 2);
    for (int i = 0; i < times.length; i = i + increment) {
      Real forward;
      Real back;
      Real forwardExpect;
      Real backExpect;

      if (duration) {
        forward = converter.convertDuration(Wholes.valueOf(times[i]), Wholes.valueOf(times[i + 1]));
        forwardExpect = Wholes.valueOf(times[i + 2]);
        back =
            converter.convertDurationBack(Wholes.valueOf(times[i]), Wholes.valueOf(times[i + 2]));
        backExpect = Wholes.valueOf(times[i + 1]);
      } else {
        forward = converter.convert(Wholes.valueOf(times[i]));
        forwardExpect = Wholes.valueOf(times[i + 1]);
        back = converter.convertBack(Wholes.valueOf(times[i + 1]));
        backExpect = Wholes.valueOf(times[i]);
      }

      assertEquals(label + sourceTick + " to " + targetTick + " failed", forwardExpect, forward);
      assertEquals(label + targetTick + " back to " + sourceTick + " failed", backExpect, back);
    }
  }

  @Test
  void testConvertToMilliseconds() {
    assertConversion(clock, BEAT, MILLISECOND, Wholes.ZERO, false, 1, 500);
    assertConversion(clock, BEAT, MILLISECOND, Wholes.ZERO, true, 100, 1, 500);
  }

  @Test
  void testConvertHalfBeatToMilliseconds() {
    assertConversion(clock, HALF, MILLISECOND, Wholes.ZERO, false, 1, 250);
  }

  @Test
  void testConvertToMicroseconds() {
    assertConversion(clock, BEAT, MICROSECOND, Wholes.ZERO, false, 1, 500000);
    assertConversion(clock, HALF, MICROSECOND, Wholes.ZERO, false, 1, 250000);
  }

  @Test
  void testConvertToMicrosecondsWithOffset() {
    assertConversion(clock, BEAT, MICROSECOND, Wholes.ONE, false, 1, 1000000);
    assertConversion(clock, HALF, MICROSECOND, Wholes.ONE, false, 1, 500000);
  }

  @Test
  void testConvertToBeat() {
    assertConversion(clock, MICROSECOND, BEAT, Wholes.ZERO, false, 500000, 1);
    assertConversion(clock, MILLISECOND, BEAT, Wholes.ZERO, false, 500, 1);
  }

  @Test
  void testConvertToHalfBeat() {
    assertConversion(clock, MICROSECOND, HALF, Wholes.ZERO, false, 1000000, 4);
    assertConversion(clock, MILLISECOND, HALF, Wholes.ZERO, false, 1000, 4);
  }
}
