package com.purplepip.odin.series;

/**
 * Length of one unit in the time series.
 */
public class Tick {
    private TimeUnit timeUnit;
    private int numerator;
    private int denominator;

    public static Tick SECOND = new Tick(TimeUnit.SECOND);
    public static Tick MILLISECOND = new Tick(TimeUnit.SECOND, 1 , 1000);
    public static Tick MICROSECOND = new Tick(TimeUnit.SECOND, 1 , 1000000);
    public static Tick BEAT = new Tick(TimeUnit.BEAT);
    public static Tick HALF_BEAT = new Tick(TimeUnit.BEAT, 1 , 2);

    public Tick(TimeUnit timeUnit) {
        this(timeUnit, 1);
    }

    public Tick(TimeUnit timeUnit, int numerator) {
        this(timeUnit, numerator, 1);
    }

    public Tick(TimeUnit timeUnit, int numerator, int denominator) {
        this.timeUnit = timeUnit;
        this.denominator = denominator;
        this.numerator = numerator;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }
}
