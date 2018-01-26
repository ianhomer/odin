package com.purplepip.odin.server.preloaded;

import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.server.rest.EndPointSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "testPreLoaded"})
@Slf4j
public class PreLoadedOperationsRestTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private OdinSequencerConfiguration configuration;

  @Test
  public void testSequencesList() throws Exception {
    configuration.getOperationTransmitter().handle(
        new NoteOnOperation(1, 1, 99), 5
    );

    new EndPointSnapshot(mvc, "/api/operation")
        .mask("(?<=\"dateCreated\" : \")[^\"]*(?=\")")
        .expectMatch();
  }
}