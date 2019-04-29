package com.purplepip.odin.api.preloaded;

import com.purplepip.odin.api.rest.EndPointSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@PreLoadedTest
@Slf4j
class PreLoadedChannelRestTest {
  @Autowired
  private MockMvc mvc;

  @Test
  void testRestSnapshot() throws Exception {
    new EndPointSnapshot(mvc, "/api/rest/channel").expectMatch();
  }
}