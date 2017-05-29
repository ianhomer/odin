package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.sequence.Sequence;

/**
 * Abstract logic class.
 */
public abstract class AbstractFlow<S extends Sequence<A>, A> implements MutableFlow<S, A> {
  private S sequence;

  public void setSequence(S sequence) {
    this.sequence = sequence;
  }

  @Override
  public S getSequence() {
    return sequence;
  }
}
