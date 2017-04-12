package com.purplepip.odin.series;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Time Unit Converter Test.
 */
public class TimeUnitConverterTest {
    @Test
    public void testConvertToMilliseconds() {
        TimeUnitConverter converter = new TimeUnitConverter(TimeUnit.MILLISECOND, 0, 120);
        assertEquals("Beat to ms failed", 500, converter.convert(TimeUnit.BEAT, 1));
        assertEquals("Half beat to ms failed", 250, converter.convert(TimeUnit.HALF_BEAT, 1));
    }

    @Test
    public void testConvertToMicroseconds() {
        TimeUnitConverter converter = new TimeUnitConverter(TimeUnit.MICROSECOND, 0, 120);
        assertEquals("Beat to micros failed", 500000, converter.convert(TimeUnit.BEAT, 1));
        assertEquals("Half beat to micros failed", 250000, converter.convert(TimeUnit.HALF_BEAT, 1));

        converter = new TimeUnitConverter(TimeUnit.MICROSECOND, 1, 120);
        assertEquals("Beat to micros failed", 500001, converter.convert(TimeUnit.BEAT, 1));
        assertEquals("Half beat to micros failed", 250001, converter.convert(TimeUnit.HALF_BEAT, 1));
    }

    @Test
    public void testConvertToBeat() {
        TimeUnitConverter converter = new TimeUnitConverter(TimeUnit.BEAT, 0, 120);
        assertEquals("Micros to beat failed", 1, converter.convert(TimeUnit.MICROSECOND, 500000));
        assertEquals("Ms to beat failed", 1, converter.convert(TimeUnit.MILLISECOND, 500));
    }

    @Test
    public void testConvertToHalfBeat() {
        TimeUnitConverter converter = new TimeUnitConverter(TimeUnit.HALF_BEAT, 0, 120);
        assertEquals("Micros to half beat failed", 1, converter.convert(TimeUnit.MICROSECOND, 1000000));
        assertEquals("Ms to half beat failed", 1, converter.convert(TimeUnit.MILLISECOND, 1000));
    }


}