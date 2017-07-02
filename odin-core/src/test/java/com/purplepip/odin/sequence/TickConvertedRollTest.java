package com.purplepip.odin.sequence;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.tick.RuntimeTick;
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
  private Roll<Note> sequenceRuntime;

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

  private TickConvertedRoll createRoll(Tick input, Tick output, int offset) {
    return new TickConvertedRoll(sequenceRuntime,
        new DefaultTickConverter(clock,
            () -> new RuntimeTick(input),
            () -> new RuntimeTick(output), () -> offset));
  }

  @Test
  public void testNoteConversionBeatToMillis() {
    TickConvertedRoll convertedSequenceRuntime =
        createRoll(Ticks.BEAT, Ticks.MILLISECOND, 10);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,3), 5));
    Event<Note> eventNote = convertedSequenceRuntime.pop();
    assertEquals("event time not correct",15000, eventNote.getTime());
    assertEquals("note duration not correct", 3000, eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionBeatToBeats() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.BEAT, Ticks.BEAT, 10);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,3), 5));
    Event<Note> eventNote = convertedRoll.pop();
    assertEquals("event time not correct",15, eventNote.getTime());
    assertEquals("note duration not correct", 3, eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionMillisToBeats() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.MILLISECOND, Ticks.BEAT, 5000);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,7000), 4000));
    Event<Note> eventNote = convertedRoll.pop();
    assertEquals("event time not correct",9, eventNote.getTime());
    assertEquals("note duration not correct", 7, eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionMillisToMillis() {
    TickConvertedRoll convertedRoll =
        createRoll(Ticks.MILLISECOND, Ticks.MILLISECOND, 5000);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,7000), 4000));
    Event<Note> eventNote = convertedRoll.pop();
    assertEquals("event time not correct", 9000, eventNote.getTime());
    assertEquals("note duration not correct", 7000, eventNote.getValue().getDuration());
  }
}