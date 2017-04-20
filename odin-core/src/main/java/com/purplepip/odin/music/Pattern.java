package com.purplepip.odin.music;

import com.purplepip.odin.series.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern
 */
public class Pattern implements Series<Note> {
  private static final Logger LOG = LoggerFactory.getLogger(Pattern.class);

  private MeasureProvider measureProvider;
  private Tick tick;
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int pattern;
  private Note note;
  private Event<Note> nextEvent;
  private long time = 0;

  public Pattern(MeasureProvider measureProvider, Tick tick) {
    this(measureProvider, tick,
        (int) Math.pow(2, (measureProvider.getBeatsInThisMeasure(new Tock(tick, 0))
            * tick.getDenominator() / tick.getNumerator())) - 1);
  }

  public Pattern(MeasureProvider measureProvider, Tick tick, int pattern) {
    this(measureProvider, tick, pattern, new DefaultNote());
  }

  public Pattern(MeasureProvider measureProvider, Tick tick, int pattern, Note note) {
    this.measureProvider = measureProvider;
    this.tick = tick;
    this.pattern = pattern;
    this.note = note;
    LOG.debug("Created pattern {} for note {}", pattern, note);
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
  public Tick getTick() {
    return tick;
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
    long maxForwardScan = 2 * measureProvider.getBeatsInThisMeasure(new Tock(getTick(), time));
    int i = 0;
    while (!on && i < maxForwardScan) {
      time++;
      i++;
      long position = measureProvider.getTickPositionInThisMeasure(new Tock(getTick(), time));
      on = ((pattern >> position) & 1) == 1;
    }
    if (on) {
      nextEvent = new DefaultEvent<>(note, time);
    } else {
      LOG.debug("No notes found in the next {} ticks", maxForwardScan);
      nextEvent = null;
    }
  }
}
