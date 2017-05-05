package com.purplepip.odin.music;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequence;
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
public class Metronome extends MutableSequence<MetronomeConfiguration> {
  private static final Logger LOG = LoggerFactory.getLogger(Metronome.class);

  private Event<Note> nextEvent;
  private long time = 0;
  private long length;


  public Metronome() {
    LOG.debug("Creating Metronome");
  }

  @Override
  public void reload() {
    TickConverter converter = new SameTimeUnitTickConverter(Tick.BEAT, Tick.HALF);
    this.length = converter.convert(getConfiguration().getLength());
  }

  @Override
  public void setMeasureProvider(MeasureProvider measureProvider) {
    super.setMeasureProvider(measureProvider);
    createNextEvent();
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
    if (getMeasureProvider().getTickPositionInThisMeasure(new Tock(getTick(), time)) == 0) {
      note = getConfiguration().getNoteBarStart();
    } else {
      note = getConfiguration().getNoteMidBar();
    }
    nextEvent = new DefaultEvent<>(note, time);
  }
}
