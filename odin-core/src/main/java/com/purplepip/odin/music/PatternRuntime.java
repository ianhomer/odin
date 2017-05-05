package com.purplepip.odin.music;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.ScanForwardEvent;
import com.purplepip.odin.sequence.Tock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PatternRuntime.
 */
public class PatternRuntime extends MutableSequenceRuntime<Pattern> {
  private static final Logger LOG = LoggerFactory.getLogger(PatternRuntime.class);

  // TODO : Move createNextEvent onto sequence class (i.e. bound to configuration) and remove the
  // need for creating a sequence runtime for each sequence definition.
  @Override
  protected Event<Note> createNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MutableTock mutableTock = new MutableTock(tock);
    Event<Note> nextEvent;
    LOG.trace("Creating next event for time {}", mutableTock.getCount());
    boolean on = false;
    long maxForwardScan = 2 * getMeasureProvider().getBeatsInThisMeasure(mutableTock);
    int i = 0;
    while (!on && i < maxForwardScan) {
      mutableTock.increment();
      i++;
      long position = getMeasureProvider().getTickPositionInThisMeasure(mutableTock);
      if (getConfiguration().getPattern() == -1) {
        on = true;
      } else {
        on = ((getConfiguration().getPattern() >> position) & 1) == 1;
      }
    }

    if (on) {
      nextEvent = new DefaultEvent<>(getConfiguration().getNote(), mutableTock.getCount());
    } else {
      LOG.debug("No notes found in the next {} ticks for pattern {}", maxForwardScan,
          getConfiguration().getPattern());
      nextEvent = new ScanForwardEvent(mutableTock.getCount());
    }
    return nextEvent;
  }
}
