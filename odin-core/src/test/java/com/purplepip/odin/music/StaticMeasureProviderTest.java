package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.sequence.ImmutableTock;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Static measure provider test.
 */
public class StaticMeasureProviderTest {
  private MeasureProvider measureProvider;

  @Before
  public void setUp() {
    measureProvider = new StaticMeasureProvider(4);
  }

  @Test
  public void testMeasure() {
    // TODO : Add tests to get millisecond in measure as this will verify that converter logic
    // implemented in measure provider, since at this time it is not implemented.

    assertEquals(1, getTickPosition(Ticks.BEAT, 9));
    assertEquals(0, getTickPosition(Ticks.BEAT, 12));
    assertEquals(0, getTickPosition(Ticks.HALF, 0));
    assertEquals(1, getTickPosition(Ticks.HALF, 1));
    assertEquals(7, getTickPosition(Ticks.HALF, 7));
    assertEquals(4, getTickPosition(Ticks.HALF, 12));
    assertEquals(0, getTickPosition(Ticks.FOUR_THIRDS, 9));
    assertEquals(1, getTickPosition(Ticks.FOUR_THIRDS, 10));

    assertEquals(0, measureProvider
        .getMeasureCount(new ImmutableTock(Ticks.BEAT, 0)));
    assertEquals(2, measureProvider
        .getMeasureCount(new ImmutableTock(Ticks.BEAT, 8)));

    assertEquals(4, measureProvider
        .getBeats(new ImmutableTock(Ticks.BEAT, 0)));
  }

  private long getTickPosition(Tick tick, long count) {
    return measureProvider.getTickPosition(new ImmutableTock(tick, count));
  }
}