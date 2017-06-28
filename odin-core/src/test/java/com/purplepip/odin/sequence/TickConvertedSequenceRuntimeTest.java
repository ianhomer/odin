package com.purplepip.odin.sequence;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * TickConvertedSequenceRuntime test.
 */
@RunWith(MockitoJUnitRunner.class)
public class TickConvertedSequenceRuntimeTest {
  @Mock
  private MicrosecondPositionProvider provider;

  @Mock
  private SequenceRuntime<Note> sequenceRuntime;

  private Clock clock;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    when(provider.getMicrosecondPosition()).thenReturn((long) 0);
    clock = new Clock(new StaticBeatsPerMinute(60), provider);
    clock.start();
  }

  private TickConvertedSequenceRuntime createRuntime(Tick input, Tick output, int offset) {
    return new TickConvertedSequenceRuntime(sequenceRuntime,
        new DefaultTickConverter(clock,
            new ImmutableRuntimeTick(input),
            new ImmutableRuntimeTick(output), () -> offset));
  }

  @Test
  public void testNoteConversionBeatToMillis() {
    TickConvertedSequenceRuntime convertedSequenceRuntime =
        createRuntime(Ticks.BEAT, Ticks.MILLISECOND, 10);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,3), 5));
    Event<Note> eventNote = convertedSequenceRuntime.pop();
    assertEquals("event time not correct",15000, eventNote.getTime());
    assertEquals("note duration not correct", 3000, eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionBeatToBeats() {
    TickConvertedSequenceRuntime convertedSequenceRuntime =
        createRuntime(Ticks.BEAT, Ticks.BEAT, 10);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,3), 5));
    Event<Note> eventNote = convertedSequenceRuntime.pop();
    assertEquals("event time not correct",15, eventNote.getTime());
    assertEquals("note duration not correct", 3, eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionMillisToBeats() {
    TickConvertedSequenceRuntime convertedSequenceRuntime =
        createRuntime(Ticks.MILLISECOND, Ticks.BEAT, 5000);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,7000), 4000));
    Event<Note> eventNote = convertedSequenceRuntime.pop();
    assertEquals("event time not correct",9, eventNote.getTime());
    assertEquals("note duration not correct", 7, eventNote.getValue().getDuration());
  }

  @Test
  public void testNoteConversionMillisToMillis() {
    TickConvertedSequenceRuntime convertedSequenceRuntime =
        createRuntime(Ticks.MILLISECOND, Ticks.MILLISECOND, 5000);
    when(sequenceRuntime.pop()).thenReturn(
        new DefaultEvent<>(
            new DefaultNote(1,1,7000), 4000));
    Event<Note> eventNote = convertedSequenceRuntime.pop();
    assertEquals("event time not correct", 9000, eventNote.getTime());
    assertEquals("note duration not correct", 7000, eventNote.getValue().getDuration());
  }
}