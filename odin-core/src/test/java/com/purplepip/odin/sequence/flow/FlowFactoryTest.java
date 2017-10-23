package com.purplepip.odin.sequence.flow;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.SequenceConfiguration;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Flow factory test.
 */
@RunWith(MockitoJUnitRunner.class)
public class FlowFactoryTest {
  @Mock
  private SequenceConfiguration sequence;

  @Mock
  private Clock clock;

  @Mock
  private MeasureProvider measureProvider;

  @Test
  public void testCreateFlow() throws OdinException {
    FlowFactory<Note> flowFactory = new FlowFactory<>(new DefaultFlowConfiguration());
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addMetronome();
    Flow<SequenceConfiguration, Note> flow =
        flowFactory.createFlow(project.getSequences().iterator().next(), clock, measureProvider);
    assertEquals("MetronomeFlow", flow.getClass().getSimpleName());
  }
}