package com.purplepip.odin.sequence.logic;

/**
 * Abstract logic class.
 */
public abstract class AbstractLogic<S, A> implements Logic<S, A> {
  private S sequence;

  protected void setSequence(S sequence) {
    this.sequence = sequence;
  }

  @Override
  public S getSequence() {
    return sequence;
  }
}
