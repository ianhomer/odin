package com.purplepip.odin.sequence;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.sequence.logic.Logic;

/**
 * Persistable sequence configuration.
 */
public interface Sequence {
  Tick getTick();

  long getLength();

  Logic createEventProvider(MeasureProvider measureProvider);
}
