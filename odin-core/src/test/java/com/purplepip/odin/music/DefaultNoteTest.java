package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequencer.SequenceBuilder;
import org.junit.Test;

/**
 * Default note test.
 */
public class DefaultNoteTest {
  @Test
  public void testDefaultNote() throws OdinException {
    Project project = new TransientProject();
    SequenceBuilder builder = new SequenceBuilder(project);
    builder.addPattern(Ticks.BEAT, 1);
    Note note = ((Pattern) project.getSequences().iterator().next()).getNote();
    assertEquals("Default note number not correct", 60, note.getNumber());
    assertEquals("Default note velocity not correct", 40, note.getVelocity());
  }
}