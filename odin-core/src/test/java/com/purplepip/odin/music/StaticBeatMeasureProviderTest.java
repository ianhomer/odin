package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import org.junit.Before;
import org.junit.Test;

/**
 * Static measure provider test.
 */
public class StaticBeatMeasureProviderTest {
  private MeasureProvider measureProvider;

  @Before
  public void setUp() {
    measureProvider = new StaticBeatMeasureProvider(4);
  }

  @Test
  public void testMeasure() {
    assertEquals(Wholes.ONE, measureProvider.getCount(Whole.valueOf(9)));
    assertEquals(Wholes.ZERO, measureProvider.getCount(Whole.valueOf(12)));

    assertEquals(Wholes.ZERO, measureProvider.getMeasure(Wholes.ZERO));
    assertEquals(Wholes.TWO, measureProvider.getMeasure(Whole.valueOf(8)));

    assertEquals(Whole.valueOf(4), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }

  @Test
  public void testMeasureStart() {
    assertEquals(Whole.valueOf(12), measureProvider.getNextMeasureStart(Whole.valueOf(9)));
  }
}