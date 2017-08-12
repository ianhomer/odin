package com.purplepip.odin.server;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "noStore"})
public class SystemControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testSystemEndPoints() throws Exception {
    assertEndPoint("/system");
    assertEndPoint("/system/synthesizer");
    assertEndPoint("/system/sequencer");
  }

  private void assertEndPoint(String endpoint) throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(endpoint)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(CoreMatchers.containsString("odin")));
  }
}