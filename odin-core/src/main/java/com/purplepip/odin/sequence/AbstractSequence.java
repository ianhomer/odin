package com.purplepip.odin.sequence;

/**
 * Abstract sequence.
 */
public abstract class AbstractSequence<A> implements MutableSequence<A> {
  private Tick tick;
  private long length = -1;
  private long offset;
  private int channel;
  private String flowName;

  @Override
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
  @Override
  public void setLength(long length) {
    this.length = length;
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public void setOffset(long offset) {
    this.offset = offset;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public void setChannel(int channel) {
    this.channel = channel;
  }

  @Override
  public int getChannel() {
    return channel;
  }

  @Override
  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  @Override
  public String getFlowName() {
    return flowName;
  }
}
