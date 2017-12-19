package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
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
    assertEquals(Wholes.ONE, measureProvider.getCount(Wholes.valueOf(9)));
    assertEquals(Wholes.ZERO, measureProvider.getCount(Wholes.valueOf(12)));

    assertEquals(Wholes.ZERO, measureProvider.getMeasure(Wholes.ZERO));
    assertEquals(Wholes.TWO, measureProvider.getMeasure(Wholes.valueOf(8)));

    assertEquals(Wholes.valueOf(4), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }

  @Test
  public void testMeasureStart() {
    assertEquals(Wholes.valueOf(12), measureProvider.getNextMeasureStart(Wholes.valueOf(9)));
  }
}