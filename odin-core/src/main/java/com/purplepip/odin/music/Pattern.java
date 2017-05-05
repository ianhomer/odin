package com.purplepip.odin.music;

import com.purplepip.odin.sequence.AbstractSequence;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.ScanForwardEvent;
import com.purplepip.odin.sequence.SequenceEventProvider;
import com.purplepip.odin.sequence.Tock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PatternRuntime configuration.
 */
public class Pattern extends AbstractSequence {
  private static final Logger LOG = LoggerFactory.getLogger(MutableSequenceRuntime.class);

  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int pattern;
  private Note note;

  public void setPattern(int pattern) {
    this.pattern = pattern;
  }

  public int getPattern() {
    return pattern;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public Note getNote() {
    return note;
  }

  @Override
  public SequenceEventProvider createEventProvider(final MeasureProvider measureProvider) {
    return new SequenceEventProvider() {
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
          if (Pattern.this.getPattern() == -1) {
            on = true;
          } else {
            on = ((Pattern.this.getPattern() >> position) & 1) == 1;
          }
        }

        if (on) {
          nextEvent = new DefaultEvent<>(Pattern.this.getNote(), mutableTock.getCount());
        } else {
          LOG.debug("No notes found in the next {} ticks for pattern {}", maxForwardScan,
              Pattern.this.getPattern());
          nextEvent = new ScanForwardEvent(mutableTock.getCount());
        }
        return nextEvent;
      }
    };
  }
}
