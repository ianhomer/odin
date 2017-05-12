package com.purplepip.odin.sequence.logic;

import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * A sequence logic class has the intelligence to determine the progression of the sequence
 * based on the sequence configuration.
 */
public interface Logic<S extends Sequence, A> {
  Event<A> getNextEvent(Tock tock, MeasureProvider measureProvider);

  S getSequence();
}
