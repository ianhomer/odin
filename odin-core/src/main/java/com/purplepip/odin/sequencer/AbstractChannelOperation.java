package com.purplepip.odin.sequencer;

/**
 * Channel operation.
 */
public abstract class AbstractChannelOperation implements ChannelOperation {
  private int channel;

  protected void setChannel(int channel) {
    this.channel = channel;
  }

  public int getChannel() {
    return channel;
  }
}

