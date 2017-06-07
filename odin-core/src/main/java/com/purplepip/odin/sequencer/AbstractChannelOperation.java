package com.purplepip.odin.sequencer;

import lombok.ToString;

/**
 * Channel operation.
 */
@ToString
public abstract class AbstractChannelOperation implements ChannelOperation {
  private int channel;

  protected void setChannel(int channel) {
    this.channel = channel;
  }

  public int getChannel() {
    return channel;
  }
}

