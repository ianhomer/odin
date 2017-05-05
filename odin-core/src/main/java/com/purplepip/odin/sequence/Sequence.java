package com.purplepip.odin.sequence;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.sequence.logic.Logic;

/**
 * Persistable sequence configuration.
 */
public interface Sequence<A> {
  Tick getTick();

  long getLength();

  Logic<A> createEventProvider(MeasureProvider measureProvider);
}
