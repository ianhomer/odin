package com.purplepip.odin.sequence;

import com.purplepip.odin.sequence.logic.Logic;

/**
 * Default sequence runtime.
 */
public class DefaultSequenceRuntime<S extends Sequence, A> extends MutableSequenceRuntime<S, A> {
  private Logic eventProvider;

  public DefaultSequenceRuntime(Logic<A> eventProvider) {
    this.eventProvider = eventProvider;
  }

  @Override
  protected Event<A> getNextEvent(Tock tock) {
    return eventProvider.getNextEvent(tock);
  }
}
