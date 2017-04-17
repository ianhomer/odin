package com.purplepip.odin.music;

import com.purplepip.odin.series.Tick;
import com.purplepip.odin.series.Tock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Static measure provider test.
 */
public class StaticMeasureProviderTest {
    @Test
    public void testMeasure() {
        MeasureProvider measureProvider = new StaticMeasureProvider(4);
        assertEquals(1, measureProvider.getTickPositionInThisMeasure(new Tock(Tick.BEAT, 9)));
        assertEquals(0, measureProvider.getTickPositionInThisMeasure(new Tock(Tick.BEAT, 12)));
        assertEquals(0, measureProvider.getTickPositionInThisMeasure(new Tock(Tick.HALF, 0)));
        assertEquals(1, measureProvider.getTickPositionInThisMeasure(new Tock(Tick.HALF, 1)));
        assertEquals(7, measureProvider.getTickPositionInThisMeasure(new Tock(Tick.HALF, 7)));
        assertEquals(4, measureProvider.getTickPositionInThisMeasure(new Tock(Tick.HALF, 12)));
        assertEquals(0, measureProvider.getMeasureCountForTock(new Tock(Tick.BEAT,0)));
        assertEquals(2, measureProvider.getMeasureCountForTock(new Tock(Tick.BEAT, 8)));
        assertEquals(4, measureProvider.getBeatsInThisMeasure(new Tock(Tick.BEAT, 0)));
    }
}