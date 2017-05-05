package com.purplepip.odin.music;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.SameTimeUnitTickConverter;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.Tock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MetronomeRuntime SequenceRuntime.
 */
public class MetronomeRuntime extends MutableSequenceRuntime<Metronome> {
  private static final Logger LOG = LoggerFactory.getLogger(MetronomeRuntime.class);

  @Override
  protected Event<Note> createNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MutableTock mutableTock = new MutableTock(tock);
    mutableTock.increment(2);
    LOG.trace("Creating next event for time {}", mutableTock.getCount());
    Note note;
    if (getMeasureProvider().getTickPositionInThisMeasure(mutableTock) == 0) {
      note = getConfiguration().getNoteBarStart();
    } else {
      note = getConfiguration().getNoteMidBar();
    }
    return new DefaultEvent<>(note, mutableTock.getCount());
  }
}
