package com.purplepip.odin.sequence;

import com.purplepip.odin.sequence.logic.Logic;

/**
 * Default sequence runtime.
 */
public class DefaultSequenceRuntime<S extends Sequence, A> extends MutableSequenceRuntime<S, A> {
  private Logic logic;

  public DefaultSequenceRuntime(Logic<S, A> logic) {
    this.logic = logic;
  }

  @Override
  protected Event<A> getNextEvent(Tock tock) {
    return logic.getNextEvent(tock);
  }
}
