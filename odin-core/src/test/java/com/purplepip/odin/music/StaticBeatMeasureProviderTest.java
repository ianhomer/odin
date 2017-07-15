package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

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
    assertEquals(1, measureProvider.getCount(9),0.001);
    assertEquals(0, measureProvider.getCount(12),0.001);

    assertEquals(0, measureProvider
        .getMeasure(0), 0.001);
    assertEquals(2, measureProvider
        .getMeasure(8),0.001);

    assertEquals(4, measureProvider.getTicksInMeasure(0), 0.001);
  }
}