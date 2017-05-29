package com.purplepip.odin.sequence.logic;

import com.purplepip.odin.sequence.Sequence;

/**
 * Abstract logic class.
 */
public abstract class AbstractLogic<S extends Sequence, A> implements Logic<S, A> {
  private S sequence;

  protected void setSequence(S sequence) {
    this.sequence = sequence;
  }

  @Override
  public S getSequence() {
    return sequence;
  }
}
