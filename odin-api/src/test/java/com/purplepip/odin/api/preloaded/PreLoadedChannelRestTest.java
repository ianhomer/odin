package com.purplepip.odin.api.preloaded;

import com.purplepip.odin.api.rest.EndPointSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@PreLoadedTest
@Slf4j
public class PreLoadedChannelRestTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void testRestSnapshot() throws Exception {
    new EndPointSnapshot(mvc, "/api/rest/channel").expectMatch();
  }
}