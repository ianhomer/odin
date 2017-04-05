package com.purplepip.odin.series;

/**
 * Default Event
 */
public class DefaultEvent<A> implements Event<A> {
    private A value;
    private long time;

    public DefaultEvent(A value, long time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public A getValue() {
        return value;
    }

    @Override
    public long getTime() {
        return time;
    }
}
