package com.purplepip.odin.sequencer;

/**
 * Operation that is ready to be fired when time is right.
 * <p>
 * Note that this is bias towards music, but it will be abstracted at some point to other operations.
 */
public class Operation {
  private OperationType type;
  private int channel;
  private int number;
  private int velocity;

  public Operation(OperationType type, int channel, int number, int velocity) {
    this.type = type;
    this.channel = channel;
    this.number = number;
    this.velocity = velocity;
  }

  public OperationType getType() {
    return type;
  }

  public int getChannel() {
    return channel;
  }

  public int getNumber() {
    return number;
  }

  public int getVelocity() {
    return velocity;
  }
}
