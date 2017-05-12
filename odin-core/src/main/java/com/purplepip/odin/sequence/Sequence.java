package com.purplepip.odin.sequence;

import com.purplepip.odin.sequence.logic.Logic;

/**
 * Persistable sequence configuration.
 */
public interface Sequence<A> {
  Tick getTick();

  long getLength();

  long getOffset();

  int getChannel();

  Logic<Sequence<A>, A> getLogic();
}
