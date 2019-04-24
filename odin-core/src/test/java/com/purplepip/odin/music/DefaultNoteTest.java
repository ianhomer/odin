package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import org.junit.Test;


/**
 * Default note test.
 */
public class DefaultNoteTest {
  @Test
  public void testDefaultNote() {
    Performance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new DefaultPerformanceContainer(project));
    builder.addPattern(Ticks.BEAT, 1);
    Note note = ((Pattern) project.getSequences().iterator().next()).getNote();
    assertEquals("Default note number not correct", 60, note.getNumber());
    assertEquals("Default note velocity not correct", 40, note.getVelocity());
  }

  @Test
  public void testEquals() {
    assertEquals(new DefaultNote(69,40,1),
        new DefaultNote(69,40,1));
  }
}