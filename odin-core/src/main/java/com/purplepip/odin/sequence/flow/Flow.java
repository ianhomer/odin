package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * A flow class has the intelligence to determine the next events in a sequence.
 */
public interface Flow<S extends Sequence<A>, A> {
  Event<A> getNextEvent(Tock tock, MeasureProvider measureProvider);

  S getSequence();
}
