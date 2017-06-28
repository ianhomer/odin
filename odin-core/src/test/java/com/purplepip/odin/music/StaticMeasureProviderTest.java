package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.sequence.ImmutableTock;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import org.junit.Test;

/**
 * Static measure provider test.
 */
public class StaticMeasureProviderTest {
  @Test
  public void testMeasure() {
    // TODO : Add tests to get millisecond in measure as this will verify that converter logic
    // implemented in measure provider, since at this time it is not implemented.
    MeasureProvider measureProvider = new StaticMeasureProvider(4);

    assertEquals(1, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.BEAT, 9)));
    assertEquals(0, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.BEAT, 12)));
    assertEquals(0, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.HALF, 0)));
    assertEquals(1, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.HALF, 1)));
    assertEquals(7, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.HALF, 7)));
    assertEquals(4, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.HALF, 12)));
    assertEquals(0, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.FOUR_THIRDS, 9)));
    assertEquals(1, measureProvider
        .getTickPosition(new ImmutableTock(Ticks.FOUR_THIRDS, 10)));

    assertEquals(0, measureProvider
        .getMeasureCount(new ImmutableTock(Ticks.BEAT, 0)));
    assertEquals(2, measureProvider
        .getMeasureCount(new ImmutableTock(Ticks.BEAT, 8)));

    assertEquals(4, measureProvider
        .getBeats(new ImmutableTock(Ticks.BEAT, 0)));
  }
}