package com.purplepip.odin.series;

/**
 * Default microsecond position provider.
 */
public class DefaultMicrosecondPositionProvider implements MicrosecondPositionProvider {
  private long microsecondsStart;

  public DefaultMicrosecondPositionProvider() {
    microsecondsStart = System.nanoTime() / 1000;
  }


  @Override
  public long getMicrosecondPosition() {
    return System.nanoTime() / 1000 - microsecondsStart;
  }
}
