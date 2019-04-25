package com.purplepip.odin.api.preloaded;

import com.purplepip.flaky.FlakyTest;
import com.purplepip.odin.api.rest.EndPointSnapshot;
import com.purplepip.odin.api.rest.repositories.OperationRepository;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@PreLoadedTest
@Slf4j
class PreLoadedOperationsRestTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private OperationRepository operationRepository;

  @Autowired
  private OdinSequencerConfiguration configuration;

  @Before
  public void before() {
    operationRepository.deleteAll();
  }

  @FlakyTest(3)
  void testRestSnapshot() throws Exception {
    send(new NoteOnOperation(1, 1, 99), 5);
    send(new NoteOffOperation(1, 1), 10);

    new EndPointSnapshot(mvc, "/api/rest/operation")
        .mask("(?<=\"dateCreated\" : \")[^\"]*(?=\")")
        .expectMatch();
  }

  private void send(Operation operation, long time) throws OdinException {
    configuration.getOperationTransmitter().handle(operation, time);
  }
}