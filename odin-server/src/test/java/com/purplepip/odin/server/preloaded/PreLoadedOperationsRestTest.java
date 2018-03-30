package com.purplepip.odin.server.preloaded;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.server.rest.EndPointSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@PreLoadedTest
@Slf4j
public class PreLoadedOperationsRestTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private OdinSequencerConfiguration configuration;

  private void send(Operation operation, long time) throws OdinException {
    configuration.getOperationTransmitter().handle(operation, time);
  }

  @Test
  public void testRestSnapshot() throws Exception {
    send(new NoteOnOperation(1, 1, 99), 5);
    send(new NoteOffOperation(1, 1), 10);

    new EndPointSnapshot(mvc, "/api/rest/operation")
        .mask("(?<=\"dateCreated\" : \")[^\"]*(?=\")")
        .expectMatch();
  }
}