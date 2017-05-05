package com.purplepip.odin.sequence;

/**
 * Abstract sequence.
 */
public abstract class AbstractSequence implements Sequence {
  private Tick tick;
  private long length = -1;

  public void setTick(Tick tick) {
    this.tick = tick;
  }

  public Tick getTick() {
    return tick;
  }

  /**
   * Set the length of the series in ticks.
   *
   * @param length length of series in ticks
   */
  public void setLength(long length) {
    this.length = length;
  }

  @Override
  public long getLength() {
    return length;
  }
}
