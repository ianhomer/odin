package com.purplepip.odin.series;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Time Unit Converter Test.
 */
public class TickConverterTest {
    @Test
    public void testConvertToMilliseconds() {
        TickConverter converter = new TickConverter(Tick.MILLISECOND, 0, 120);
        assertEquals("Beat to ms failed", 500, converter.convert(Tick.BEAT, 1));
        assertEquals("Half beat to ms failed", 250, converter.convert(Tick.HALF_BEAT, 1));
    }

    @Test
    public void testConvertToMicroseconds() {
        TickConverter converter = new TickConverter(Tick.MICROSECOND, 0, 120);
        assertEquals("Beat to micros failed", 500000, converter.convert(Tick.BEAT, 1));
        assertEquals("Half beat to micros failed", 250000, converter.convert(Tick.HALF_BEAT, 1));

        converter = new TickConverter(Tick.MICROSECOND, 1, 120);
        assertEquals("Beat to micros failed", 500001, converter.convert(Tick.BEAT, 1));
        assertEquals("Half beat to micros failed", 250001, converter.convert(Tick.HALF_BEAT, 1));
    }

    @Test
    public void testConvertToBeat() {
        TickConverter converter = new TickConverter(Tick.BEAT, 0, 120);
        assertEquals("Micros to beat failed", 1, converter.convert(Tick.MICROSECOND, 500000));
        assertEquals("Ms to beat failed", 1, converter.convert(Tick.MILLISECOND, 500));
    }

    @Test
    public void testConvertToHalfBeat() {
        TickConverter converter = new TickConverter(Tick.HALF_BEAT, 0, 120);
        assertEquals("Micros to half beat failed", 1, converter.convert(Tick.MICROSECOND, 1000000));
        assertEquals("Ms to half beat failed", 1, converter.convert(Tick.MILLISECOND, 1000));
    }


}