package com.purplepip.odin.sequence;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * TickConvertedRoll test.
 */
@RunWith(MockitoJUnitRunner.class)
public class TickConvertedRollTest {
  @Mock
  private MicrosecondPositionProvider provider;

  @Mock
  private Roll<Note> roll;

  private BeatClock clock;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    when(provider.getMicroseconds()).thenReturn((long) 0);
    clock = new BeatClock(new StaticBeatsPerMinute(60), provider);
    clock.start();
  }

  private TickConvertedRoll createRoll(Tick input, Tick output, long offset) {
    return new TickConvertedRoll(roll,
        new DefaultTickConverter(clock,
            () -> input,
            () -> output, () -> offset));
  }

  @Test
  public void testNoteConversionBeatToMillis() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.BEAT, Ticks.MILLISECOND, 10);
    when(roll.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,3), 5));
    Event<Note> eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Whole.valueOf(15000),
        eventNote.getTime());
    assertEquals("note duration not correct", Whole.valueOf(3000),
        eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionBeatToBeats() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.BEAT, Ticks.BEAT, 10);
    when(roll.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,3), 5));
    Event<Note> eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Whole.valueOf(15),
        eventNote.getTime());
    assertEquals("note duration not correct", Whole.valueOf(3), eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionMillisToBeats() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.MILLISECOND, Ticks.BEAT, 5000);
    when(roll.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,7000), 4000));
    Event<Note> eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Whole.valueOf(9), eventNote.getTime());
    assertEquals("note duration not correct", Whole.valueOf(7), eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionMillisToMillis() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.MILLISECOND, Ticks.MILLISECOND, 5000);
    when(roll.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,7000), 4000));
    Event<Note> eventNote = convertedRoll.pop();
    assertEquals("event time not correct", Whole.valueOf(9000),
        eventNote.getTime());
    assertEquals("note duration not correct", Whole.valueOf(7000),
        eventNote.getValue().getDuration());
  }
}