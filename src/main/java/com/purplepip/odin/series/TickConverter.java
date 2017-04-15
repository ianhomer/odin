package com.purplepip.odin.series;

/**
 * Tick converter.
 */
public interface TickConverter {
    Tick getOutputTick();

    long convert(long time)  ;
}
