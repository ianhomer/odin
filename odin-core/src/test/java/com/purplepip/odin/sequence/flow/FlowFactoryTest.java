package com.purplepip.odin.sequence.flow;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.SequenceBuilder;
import org.junit.Test;

/**
 * Flow factory test.
 */
public class FlowFactoryTest {
  @Test
  public void testCreateFlow() throws OdinException {
    FlowFactory<Note> flowFactory = new FlowFactory<>();
    TransientProject project = new TransientProject();
    SequenceBuilder builder = new SequenceBuilder(project);
    builder.addMetronome();
    Flow<Sequence<Note>, Note> flow =
        flowFactory.createFlow(project.getSequences().iterator().next());
    assertEquals("MetronomeFlow", flow.getClass().getSimpleName());
  }
}