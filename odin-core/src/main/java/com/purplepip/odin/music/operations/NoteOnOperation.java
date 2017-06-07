package com.purplepip.odin.music.operations;

/**
 * NoteOnOperation that is ready to be fired when time is right.
 *
 * <p>Note that this is bias towards music, but it will be abstracted at some point to other
 * operations.
 */
public class NoteOnOperation extends NoteVelocityOperation {

  /**
   * Create an operation.
   *
   * @param channel Channel for the operation
   * @param number Number for the operation
   * @param velocity Velocity for the operation
   */
  public NoteOnOperation(int channel, int number, int velocity) {
    setChannel(channel);
    setNumber(number);
    setVelocity(velocity);
  }
}
