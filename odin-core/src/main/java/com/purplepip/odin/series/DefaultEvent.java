package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Event
 */
public class DefaultEvent<A> implements Event<A> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEvent.class);
    private A value;
    private long time;

    public DefaultEvent(A value, long time) {
        LOG.trace("Creating new event : {} at time {}", value, time);
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
