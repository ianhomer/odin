package com.purplepip.odin.sequence.flow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.FailOverFlow;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test for unconstructible flow.
 */
@RunWith(MockitoJUnitRunner.class)
public class FlowFactoryUnconstructibleTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Mock
  private Sequence sequence;

  @Mock
  private Clock clock;

  @Mock
  private MeasureProvider measureProvider;

  @Test
  public void testCreateFlowCantInstantiate() throws OdinException {
    // TODO : Correct this test to test for instantiation error NOT registration error.
    when(sequence.getFlowName()).thenReturn("FlowCantInstantiate");
    FlowFactory<Note> flowFactory = new FlowFactory<>(new DefaultFlowConfiguration());
    try (LogCaptor captor = new LogCapture().error().from(FlowFactory.class).start()) {
      Flow flow = flowFactory.createFlow(sequence, clock, measureProvider);
      assertEquals(FailOverFlow.class.getName(), flow.getClass().getName());
      assertEquals(1, captor.size());
      assertEquals("Flow class FlowCantInstantiate not registered",
          captor.getMessage(0));
    }
  }
}
