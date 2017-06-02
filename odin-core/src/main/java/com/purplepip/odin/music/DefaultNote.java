package com.purplepip.odin.music;

/**
 * Default Note.
 */
public class DefaultNote implements Note {
  private int number;
  private int velocity;
  private long duration;

  /**
   * Create a default note.
   *
   * @param number Number of note
   * @param velocity Velocity of note
   * @param duration Duration of note
   */
  public DefaultNote(int number, int velocity, long duration) {
    this.number = number;
    this.velocity = velocity;
    this.duration = duration;
  }

  @Override
  public int getNumber() {
    return number;
  }

  @Override
  public int getVelocity() {
    return velocity;
  }

  @Override
  public long getDuration() {
    return duration;
  }
}
