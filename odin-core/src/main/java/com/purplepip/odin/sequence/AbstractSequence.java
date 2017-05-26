package com.purplepip.odin.sequence;

/**
 * Abstract sequence.
 */
public abstract class AbstractSequence<A> implements Sequence<A> {
  private Tick tick;
  private long length = -1;
  private long offset;
  private int channel;

  public void setTick(Tick tick) {
    this.tick = tick;
  }

  @Override
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

  public void setOffset(long offset) {
    this.offset = offset;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  @Override
  public int getChannel() {
    return channel;
  }

}
