package com.purplepip.odin.series;

/**
 * Series Event
 */
public interface Event<A> {
    A getValue();
    long getTime();
}
