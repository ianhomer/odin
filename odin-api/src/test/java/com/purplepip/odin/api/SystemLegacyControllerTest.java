package com.purplepip.odin.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "noStore", "noAuditing"})
public class SystemLegacyControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testSystemEndPoints() throws Exception {
    assertEndPoint("/api/page/system");
    assertEndPoint("/api/page/system/synthesizer");
    assertEndPoint("/api/page/system/sequencer");
  }

  private void assertEndPoint(String endpoint) throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(endpoint)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(CoreMatchers.containsString("odin")));
  }
}