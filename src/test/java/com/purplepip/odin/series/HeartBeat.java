package com.purplepip.odin.series;

/**
 * Series of heartbeats every second.
 */
public class HeartBeat implements Series<Boolean> {
    @Override
    public Event<Boolean> peek() {
        return getNext();
    }

    @Override
    public Event<Boolean> pop() {
        return getNext();
    }

    private Event<Boolean> getNext() {
        return new DefaultEvent<>(Boolean.TRUE, ((System.currentTimeMillis() + 999) / 1000) * 1000);
    }
}
