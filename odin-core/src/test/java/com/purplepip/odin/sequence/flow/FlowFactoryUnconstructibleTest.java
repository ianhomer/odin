package com.purplepip.odin.sequence.flow;

import static org.mockito.Mockito.when;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Sequence;
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

  @Test
  public void testCreateFlowCantInstantiate() throws OdinException {
    exception.expect(OdinException.class);
    exception.expectMessage("Cannot create instance");
    when(sequence.getFlowName()).thenReturn("com.purplepip.odin.sequence.flow.UnconstructibleFlow");
    FlowFactory<Note> flowFactory = new FlowFactory<>();
    flowFactory.createFlow(sequence);
  }
}
