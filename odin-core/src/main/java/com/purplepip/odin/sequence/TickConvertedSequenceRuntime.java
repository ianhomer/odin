package com.purplepip.odin.sequence;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SequenceRuntime where time is in milliseconds relative to some origin, e.g. MIDI device start
 */
public class TickConvertedSequenceRuntime implements SequenceRuntime<Note> {
  private static final Logger LOG = LoggerFactory.getLogger(TickConvertedSequenceRuntime.class);
  private SequenceRuntime<Note> sequenceRuntime;
  private DefaultTickConverter tickConverter;

  public TickConvertedSequenceRuntime(SequenceRuntime<Note> sequenceRuntime,
                                      DefaultTickConverter tickConverter) {
    this.sequenceRuntime = sequenceRuntime;
    this.tickConverter = tickConverter;
  }

  @Override
  public Event<Note> peek() {
    return convertTimeUnits(sequenceRuntime.peek());
  }

  @Override
  public Event<Note> pop() {
    return convertTimeUnits(sequenceRuntime.pop());
  }

  @Override
  public Tick getTick() {
    return tickConverter.getOutputTick();
  }

  private Event<Note> convertTimeUnits(Event<Note> event) {
    if (event == null) {
      LOG.debug("No event on sequenceRuntime to convert");
      return null;
    }
    if (tickConverter.getOutputTick() == sequenceRuntime.getTick()) {
      return event;
    }
    Note note = new DefaultNote(event.getValue().getNumber(), event.getValue().getVelocity(),
        tickConverter.convert(event.getValue().getDuration()));
    long time = tickConverter.convert(event.getTime());
    LOG.trace("Converted note {} to time {}", note, time);
    return new DefaultEvent<>(note, time);
  }

}
