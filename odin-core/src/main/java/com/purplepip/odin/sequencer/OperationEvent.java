package com.purplepip.odin.sequencer;

/**
 * Operation event that should fire at the given time.
 */
public class OperationEvent {
  private Operation operation;
  private long time;

  OperationEvent(Operation operation, long time) {
    this.operation = operation;
    this.time = time;
  }

  public long getTime() {
    return time;
  }

  public Operation getOperation() {
    return operation;
  }

}
