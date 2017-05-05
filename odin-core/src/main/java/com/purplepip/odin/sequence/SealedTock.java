package com.purplepip.odin.sequence;

/**
 * Tock that wraps another, potentially mutable, tock, but cannot mutate the tock itself.
 */
public class SealedTock implements Tock {
  private Tock tock;

  public SealedTock(Tock tock) {
    this.tock = tock;
  }

  @Override
  public Tick getTick() {
    return tock.getTick();
  }

  @Override
  public long getCount() {
    return tock.getCount();
  }
}
