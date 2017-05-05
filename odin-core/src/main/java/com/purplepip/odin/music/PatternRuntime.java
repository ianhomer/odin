package com.purplepip.odin.music;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.MutableTock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PatternRuntime.
 */
public class PatternRuntime extends MutableSequenceRuntime<Pattern> {
  private static final Logger LOG = LoggerFactory.getLogger(PatternRuntime.class);

  @Override
  protected Event<Note> createNextEvent(MutableTock tock) {
    Event<Note> nextEvent;
    LOG.trace("Creating next event for time {}", tock.getCount());
    boolean on = false;
    long maxForwardScan = 2 * getMeasureProvider().getBeatsInThisMeasure(tock);
    int i = 0;
    while (!on && i < maxForwardScan) {
      tock.increment();
      i++;
      long position = getMeasureProvider().getTickPositionInThisMeasure(tock);
      if (getConfiguration().getPattern() == -1) {
        on = true;
      } else {
        on = ((getConfiguration().getPattern() >> position) & 1) == 1;
      }
    }
    if (on) {
      nextEvent = new DefaultEvent<>(getConfiguration().getNote(), tock.getCount());
    } else {
      LOG.debug("No notes found in the next {} ticks", maxForwardScan);
      nextEvent = null;
    }
    return nextEvent;
  }
}
