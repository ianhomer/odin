package com.purplepip.odin.series;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Time Unit Converter Test.
 */
public class TickConverterTest {

    @Test
    public void testConvertToMilliseconds() {
        Clock clock = new Clock(120);
        TickConverter converter = new TickConverter(clock, Tick.BEAT, Tick.MILLISECOND, 0);
        assertEquals("Beat to ms failed", 500, converter.convert(1));
        converter = new TickConverter(clock, Tick.HALF_BEAT, Tick.MILLISECOND, 0);
        assertEquals("Half beat to ms failed", 250, converter.convert(1));
    }

    @Test
    public void testConvertToMicroseconds() {
        Clock clock = new Clock(120);
        TickConverter converter = new TickConverter(clock, Tick.BEAT, Tick.MICROSECOND, 0);
        assertEquals("Beat to micros failed", 500000, converter.convert( 1));
        converter = new TickConverter(clock, Tick.HALF_BEAT, Tick.MICROSECOND, 0);
        assertEquals("Half beat to micros failed", 250000, converter.convert(1));

        // TODO : Support offset
        //converter = new TickConverter(clock, Tick.BEAT, Tick.MICROSECOND, 1);
        //assertEquals("Beat to micros failed", 500001, converter.convert(1));
        //converter = new TickConverter(clock, Tick.HALF_BEAT, Tick.MICROSECOND, 1);
        //assertEquals("Half beat to micros failed", 250001, converter.convert(1));
    }

    @Test
    public void testConvertToBeat() {
        Clock clock = new Clock(120);
        TickConverter converter = new TickConverter(clock, Tick.MICROSECOND, Tick.BEAT, 0);
        assertEquals("Micros to beat failed", 1, converter.convert(500000));
        converter = new TickConverter(clock, Tick.MILLISECOND, Tick.BEAT, 0);
        assertEquals("Ms to beat failed", 1, converter.convert(500));
    }

    @Test
    public void testConvertToHalfBeat() {
        Clock clock = new Clock(120);
        TickConverter converter = new TickConverter(clock, Tick.MICROSECOND, Tick.HALF_BEAT, 0);
        assertEquals("Micros to half beat failed", 4, converter.convert(1000000));
        converter = new TickConverter(clock, Tick.MILLISECOND, Tick.HALF_BEAT, 0);
        assertEquals("Ms to half beat failed", 4, converter.convert(1000));
    }

}