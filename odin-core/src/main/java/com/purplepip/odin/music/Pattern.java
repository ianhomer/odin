package com.purplepip.odin.music;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Tock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern.
 */
public class Pattern extends MutableSequence<PatternConfiguration> {
  private static final Logger LOG = LoggerFactory.getLogger(Pattern.class);

  private Event<Note> nextEvent;
  private long time = 0;

  @Override
  public void setMeasureProvider(MeasureProvider measureProvider) {
    super.setMeasureProvider(measureProvider);
    createNextEvent();
  }

  @Override
  public Event<Note> peek() {
    if (nextEvent == null) {
      createNextEvent();
    }
    return nextEvent;
  }

  @Override
  public Event<Note> pop() {
    Event<Note> thisEvent = nextEvent;
    createNextEvent();
    return thisEvent;
  }

  private void createNextEvent() {
    LOG.trace("Creating next event for time {}", time);
    boolean on = false;
    long maxForwardScan = 2 * getMeasureProvider().getBeatsInThisMeasure(new Tock(getTick(), time));
    int i = 0;
    while (!on && i < maxForwardScan) {
      time++;
      i++;
      long position = getMeasureProvider().getTickPositionInThisMeasure(new Tock(getTick(), time));
      if (getConfiguration().getPattern() == -1) {
        on = true;
      } else {
        on = ((getConfiguration().getPattern() >> position) & 1) == 1;
      }
    }
    if (on) {
      nextEvent = new DefaultEvent<>(getConfiguration().getNote(), time);
    } else {
      LOG.debug("No notes found in the next {} ticks", maxForwardScan);
      nextEvent = null;
    }
  }
}
