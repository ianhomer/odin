package com.purplepip.odin.server.preloaded;

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
public class PreLoadedPerformanceRestTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void testRestSnapshot() throws Exception {
    new EndPointSnapshot(mvc, "/api/performance").expectMatch();
  }
}