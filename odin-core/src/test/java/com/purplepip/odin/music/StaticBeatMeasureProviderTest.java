package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.math.Wholes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Static measure provider test.
 */
class StaticBeatMeasureProviderTest {
  private MeasureProvider measureProvider;

  @BeforeEach
  void setUp() {
    measureProvider = new StaticBeatMeasureProvider(4);
  }

  @Test
  void testMeasure() {
    assertEquals(Wholes.ONE, measureProvider.getCount(Wholes.valueOf(9)));
    assertEquals(Wholes.ZERO, measureProvider.getCount(Wholes.valueOf(12)));

    assertEquals(Wholes.ZERO, measureProvider.getMeasure(Wholes.ZERO));
    assertEquals(Wholes.TWO, measureProvider.getMeasure(Wholes.valueOf(8)));

    assertEquals(Wholes.valueOf(4), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }

  @Test
  void testMeasureStart() {
    assertEquals(Wholes.valueOf(12), measureProvider.getNextMeasureStart(Wholes.valueOf(9)));
  }
}