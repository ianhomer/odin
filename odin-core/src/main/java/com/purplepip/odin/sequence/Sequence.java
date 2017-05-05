package com.purplepip.odin.sequence;

import com.purplepip.odin.music.MeasureProvider;

/**
 * Persistable sequence configuration.
 */
public interface Sequence {
  Tick getTick();

  long getLength();

  SequenceEventProvider createEventProvider(MeasureProvider measureProvider);
}
