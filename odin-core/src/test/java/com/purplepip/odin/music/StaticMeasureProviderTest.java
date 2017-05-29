package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.ImmutableTock;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import org.junit.Test;

/**
 * Static measure provider test.
 */
public class StaticMeasureProviderTest {
  @Test
  public void testMeasure() {
    MeasureProvider measureProvider = new StaticMeasureProvider(4);
    assertEquals(1, measureProvider
        .getTickPositionInThisMeasure(new ImmutableTock(DefaultTick.BEAT, 9)));
    assertEquals(0, measureProvider
        .getTickPositionInThisMeasure(new ImmutableTock(DefaultTick.BEAT, 12)));
    assertEquals(0, measureProvider
        .getTickPositionInThisMeasure(new ImmutableTock(DefaultTick.HALF, 0)));
    assertEquals(1, measureProvider
        .getTickPositionInThisMeasure(new ImmutableTock(DefaultTick.HALF, 1)));
    assertEquals(7, measureProvider
        .getTickPositionInThisMeasure(new ImmutableTock(DefaultTick.HALF, 7)));
    assertEquals(4, measureProvider
        .getTickPositionInThisMeasure(new ImmutableTock(DefaultTick.HALF, 12)));
    assertEquals(0, measureProvider
        .getMeasureCountForTock(new ImmutableTock(DefaultTick.BEAT, 0)));
    assertEquals(2, measureProvider
        .getMeasureCountForTock(new ImmutableTock(DefaultTick.BEAT, 8)));
    assertEquals(4, measureProvider
        .getBeatsInThisMeasure(new ImmutableTock(DefaultTick.BEAT, 0)));
  }
}