package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticBeatMeasureProvider;
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
    assertEquals(Wholes.ONE, measureProvider.getCount(Real.valueOf(9)));
    assertEquals(Wholes.ZERO, measureProvider.getCount(Real.valueOf(12)));

    assertEquals(Wholes.ZERO, measureProvider.getMeasure(Wholes.ZERO));
    assertEquals(Wholes.TWO, measureProvider.getMeasure(Real.valueOf(8)));

    assertEquals(Real.valueOf(4), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }
}