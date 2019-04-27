package com.purplepip.odin.roll;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TickConvertedRoll test.
 */
class TickConvertedRollTest {
  private Roll roll;

  private BeatClock clock;

  /**
   * Set up.
   */
  @BeforeEach
  void setUp() {
    roll = mock(Roll.class);
    MicrosecondPositionProvider provider = mock(MicrosecondPositionProvider.class);
    when(provider.getMicroseconds()).thenReturn((long) 0);
    clock = newPrecisionBeatClock(60, provider);
    clock.start();
  }

  private TickConvertedRoll createRoll(Tick input, Tick output, Rational offset) {
    return new TickConvertedRoll(roll,
        new DefaultTickConverter(clock, () -> input, () -> output, () -> offset));
  }

  @Test
  void testNoteConversionBeatToMillis() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.BEAT, Ticks.MILLISECOND, Wholes.valueOf(10));
    when(roll.pop()).thenReturn(
        new DefaultEvent(
            new DefaultNote(1,1,3), 5));
    Event eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Wholes.valueOf(15000),
        eventNote.getTime());
    assertEquals("note duration not correct", Wholes.valueOf(3000),
        ((Note) eventNote.getValue()).getDuration());
  }

  @Test
  void testNoteConversionBeatToBeats() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.BEAT, Ticks.BEAT, Wholes.valueOf(10));
    when(roll.pop()).thenReturn(
        new DefaultEvent(
            new DefaultNote(1,1,3), 5));
    Event eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Wholes.valueOf(15),
        eventNote.getTime());
    assertEquals("note duration not correct", Wholes.valueOf(3),
        ((Note) eventNote.getValue()).getDuration());
  }

  @Test
  void testNoteConversionMillisToBeats() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.MILLISECOND, Ticks.BEAT, Wholes.valueOf(5000));
    when(roll.pop()).thenReturn(
        new DefaultEvent(
            new DefaultNote(1,1,7000), 4000));
    Event eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Wholes.valueOf(9), eventNote.getTime());
    assertEquals("note duration not correct", Wholes.valueOf(7),
        ((Note) eventNote.getValue()).getDuration());
  }

  @Test
  void testNoteConversionMillisToMillis() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.MILLISECOND, Ticks.MILLISECOND, Wholes.valueOf(5000));
    when(roll.pop()).thenReturn(
        new DefaultEvent(
            new DefaultNote(1,1,7000), 4000));
    Event eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Wholes.valueOf(9000),
        eventNote.getTime());
    assertEquals("note duration not correct", Wholes.valueOf(7000),
        ((Note) eventNote.getValue()).getDuration());
  }
}