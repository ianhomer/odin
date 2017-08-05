package com.purplepip.odin.sequence.flow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.flow.FailOverFlow;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.Sequence;
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
  private Sequence sequence;

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
    Flow<Sequence, Note> flow =
        flowFactory.createFlow(project.getSequences().iterator().next(), clock, measureProvider);
    assertEquals("MetronomeFlow", flow.getClass().getSimpleName());
  }

  @Test
  public void testCreateFlowNotExists() throws OdinException {
    FlowFactory<Note> flowFactory = new FlowFactory<>(new DefaultFlowConfiguration());
    when(sequence.getFlowName()).thenReturn("FlowDoesNotExist");
    try (LogCaptor captor = new LogCapture().error().from(FlowFactory.class).start()) {
      Flow flow = flowFactory.createFlow(sequence, clock, measureProvider);
      assertEquals(FailOverFlow.class.getName(), flow.getClass().getName());
      assertEquals(1, captor.size());
      assertEquals("Flow class FlowDoesNotExist not registered",
          captor.getMessage(0));
    }
  }
}