package com.purplepip.odin.series;

/**
 * Events that occur over time.
 */
public interface Series<A> {
    Event<A> peek();
    Event<A> pop();
    Tick getTick();
}
