package com.purplepip.odin.music;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.SameTimeUnitTickConverter;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.Tock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metronome Sequence.
 */
public class Metronome implements Sequence<Note> {
  private static final Logger LOG = LoggerFactory.getLogger(Metronome.class);

  private Event<Note> nextEvent;
  private Note noteBarStart;
  private Note noteMidBar;
  private long time = 0;
  private long length;
  private MeasureProvider measureProvider;

  public Metronome(MeasureProvider measureProvider) {
    this(measureProvider, -1);
  }

  private Metronome(MeasureProvider measureProvider, long length) {
    noteBarStart = new DefaultNote();
    noteMidBar = new DefaultNote(64, noteBarStart.getVelocity() / 2);
    TickConverter converter = new SameTimeUnitTickConverter(Tick.BEAT, Tick.HALF);
    this.length = converter.convert(length);
    this.measureProvider = measureProvider;
    LOG.debug("Creating Metronome with length {} and measure provider {}",
        length, measureProvider);
    createNextEvent();
  }

  @Override
  public Tick getTick() {
    return Tick.HALF;
  }

  @Override
  public Event<Note> peek() {
    return nextEvent;
  }

  @Override
  public Event<Note> pop() {
    Event<Note> thisEvent = nextEvent;
    time = time + 2;
    if (length < 0 || time < length) {
      createNextEvent();
    } else {
      nextEvent = null;
    }
    return thisEvent;
  }


  private void createNextEvent() {
    LOG.trace("Creating next event for time {}", time);
    Note note;
    if (measureProvider.getTickPositionInThisMeasure(new Tock(getTick(), time)) == 0) {
      note = noteBarStart;
    } else {
      note = noteMidBar;
    }
    nextEvent = new DefaultEvent<>(note, time);
  }
}
