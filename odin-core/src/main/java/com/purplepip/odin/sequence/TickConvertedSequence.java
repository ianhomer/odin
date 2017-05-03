package com.purplepip.odin.sequence;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sequence where time is in milliseconds relative to some origin, e.g. MIDI device start
 */
public class TickConvertedSequence implements Sequence<Note> {
  private static final Logger LOG = LoggerFactory.getLogger(TickConvertedSequence.class);
  private Sequence<Note> sequence;
  private DefaultTickConverter tickConverter;

  public TickConvertedSequence(Sequence<Note> sequence, DefaultTickConverter tickConverter) {
    this.sequence = sequence;
    this.tickConverter = tickConverter;
  }

  @Override
  public Event<Note> peek() {
    return convertTimeUnits(sequence.peek());
  }

  @Override
  public Event<Note> pop() {
    return convertTimeUnits(sequence.pop());
  }

  @Override
  public Tick getTick() {
    return tickConverter.getOutputTick();
  }

  private Event<Note> convertTimeUnits(Event<Note> event) {
    if (event == null) {
      LOG.debug("No event on sequence to convert");
      return null;
    }
    if (tickConverter.getOutputTick() == sequence.getTick()) {
      return event;
    }
    Note note = new DefaultNote(event.getValue().getNumber(), event.getValue().getVelocity(),
        tickConverter.convert(event.getValue().getDuration()));
    long time = tickConverter.convert(event.getTime());
    LOG.trace("Converted note {} to time {}", note, time);
    return new DefaultEvent<>(note, time);
  }

}
