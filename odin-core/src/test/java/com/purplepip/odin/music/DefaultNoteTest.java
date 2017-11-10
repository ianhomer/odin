package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;

/**
 * Default note test.
 */
public class DefaultNoteTest {
  @Test
  public void testDefaultNote() throws OdinException {
    Project project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addPattern(Ticks.BEAT, 1);
    Note note = ((Pattern) project.getSequences().iterator().next()).getNote();
    assertEquals("Default note number not correct", 60, note.getNumber());
    assertEquals("Default note velocity not correct", 40, note.getVelocity());
  }
}