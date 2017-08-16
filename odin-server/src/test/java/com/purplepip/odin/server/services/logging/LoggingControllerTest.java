package com.purplepip.odin.server.services.logging;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "noStore", "noAuditing"})
@Slf4j
public class LoggingControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testGetLogging() throws Exception {
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/services/logging")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String json = result.getResponse().getContentAsString();
    LOG.info("JSON = {}", json);
  }

  @Test
  public void testSetLogging() throws Exception {
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/services/logging/com.purplepip/debug")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String json = result.getResponse().getContentAsString();
    LOG.info("JSON = {}", json);
  }

}