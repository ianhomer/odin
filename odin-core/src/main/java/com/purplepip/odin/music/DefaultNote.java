package com.purplepip.odin.music;

/**
 * Default Note
 */
public class DefaultNote implements Note {
  // TODO : Externalise default note configuration.
  private int number;
  private int velocity;
  private long duration;

  public DefaultNote() {
    this(60);
  }

  public DefaultNote(int number) {
    this(number, 40);
  }

  public DefaultNote(int number, int velocity) {
    this(number, velocity, 1);
  }

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
