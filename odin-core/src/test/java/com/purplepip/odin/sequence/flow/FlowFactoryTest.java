package com.purplepip.odin.sequence.flow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.SequenceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Flow factory test.
 */
@RunWith(MockitoJUnitRunner.class)
public class FlowFactoryTest {
  @Test
  public void testCreateFlow() throws OdinException {
    FlowFactory<Note> flowFactory = new FlowFactory<>();
    TransientProject project = new TransientProject();
    SequenceBuilder builder = new SequenceBuilder(project);
    builder.addMetronome();
    Flow<Sequence, Note> flow =
        flowFactory.createFlow(project.getSequences().iterator().next());
    assertEquals("MetronomeFlow", flow.getClass().getSimpleName());
  }

  @Mock
  private Sequence sequence;

  @Test(expected = OdinException.class)
  public void testCreateFlowNotExists() throws OdinException {
    FlowFactory<Note> flowFactory = new FlowFactory<>();
    when(sequence.getFlowName()).thenReturn("FlowDoesNotExist");
    flowFactory.createFlow(sequence);
  }
}