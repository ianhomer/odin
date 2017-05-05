package com.purplepip.odin.sequence.logic;

import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Sequence Event Provider.
 */
public interface Logic<S, A> {
  Event<A> getNextEvent(Tock tock, MeasureProvider measureProvider);

  S getSequence();
}
