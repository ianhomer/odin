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
        assertEquals(1, measureProvider.getTickPosition(new Tock(Tick.BEAT, 9)));
        assertEquals(0, measureProvider.getTickPosition(new Tock(Tick.BEAT, 12)));
        assertEquals(0, measureProvider.getTickPosition(new Tock(Tick.HALF, 0)));
        assertEquals(1, measureProvider.getTickPosition(new Tock(Tick.HALF, 1)));
        assertEquals(7, measureProvider.getTickPosition(new Tock(Tick.HALF, 7)));
        assertEquals(4, measureProvider.getTickPosition(new Tock(Tick.HALF, 12)));
        assertEquals(0, measureProvider.getMeasureCount(new Tock(Tick.BEAT,0)));
        assertEquals(2, measureProvider.getMeasureCount(new Tock(Tick.BEAT, 8)));
        assertEquals(4, measureProvider.getMeasureBeats(new Tock(Tick.BEAT, 0)));
    }
}