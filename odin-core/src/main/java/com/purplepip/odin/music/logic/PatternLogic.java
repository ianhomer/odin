package com.purplepip.odin.music.logic;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.ScanForwardEvent;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.logic.Logic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern logic.
 */
public class PatternLogic implements Logic<Note> {
  private static final Logger LOG = LoggerFactory.getLogger(PatternLogic.class);

  private Pattern pattern;
  private MeasureProvider measureProvider;

  public PatternLogic(Pattern pattern, MeasureProvider measureProvider) {
    this.pattern = pattern;
    this.measureProvider = measureProvider;
  }

  @Override
  public Event<Note> getNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MutableTock mutableTock = new MutableTock(tock);
    Event<Note> nextEvent;
    boolean on = false;
    long maxForwardScan = 2 * measureProvider.getBeatsInThisMeasure(mutableTock);
    int i = 0;
    while (!on && i < maxForwardScan) {
      mutableTock.increment();
      i++;
      long position = measureProvider.getTickPositionInThisMeasure(mutableTock);
      if (pattern.getPattern() == -1) {
        on = true;
      } else {
        on = ((pattern.getPattern() >> position) & 1) == 1;
      }
    }

    if (on) {
      nextEvent = new DefaultEvent<>(pattern.getNote(), mutableTock.getCount());
    } else {
      LOG.debug("No notes found in the next {} ticks for pattern {}", maxForwardScan,
          pattern.getPattern());
      nextEvent = new ScanForwardEvent(mutableTock.getCount());
    }
    return nextEvent;
  }
}
