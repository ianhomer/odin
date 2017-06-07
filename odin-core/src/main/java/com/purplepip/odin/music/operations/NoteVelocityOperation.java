package com.purplepip.odin.music.operations;

import com.purplepip.odin.sequencer.ChannelOperation;

/**
 * Note operation.
 */
public abstract class NoteVelocityOperation implements ChannelOperation {
  private int channel;
  private int number;
  private int velocity;

  protected void setChannel(int channel) {
    this.channel = channel;
  }

  public int getChannel() {
    return channel;
  }

  protected void setNumber(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

  protected void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  public int getVelocity() {
    return velocity;
  }

}
