package com.purplepip.odin.music.operations;

import com.purplepip.odin.sequencer.AbstractChannelOperation;

/**
 * Note operation.
 */
public abstract class NoteVelocityOperation extends AbstractChannelOperation {
  private int number;
  private int velocity;

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
