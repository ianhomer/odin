package com.purplepip.odin.server.rest;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.nio.charset.Charset;
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
@ActiveProfiles({"noAuditing"})
@Slf4j
public class SequenceRestTest {
  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  @Autowired
  private MockMvc mvc;

  @Test
  public void testGetSequences() throws Exception {
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/notations"))
        .andExpect(status().isOk())
        .andReturn();
    String json = result.getResponse().getContentAsString();
    LOG.debug("JSON = {}", json);
    DocumentContext context = JsonPath.parse(json);
    String uri = context.read("$._embedded.notations[0]._links.self.href");
    LOG.debug("Getting notation from {}", uri);

    result = mvc.perform(MockMvcRequestBuilders.get(uri))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.offset", is(0)))
        .andExpect(jsonPath("$.notation", is("A/q G/8 A/q E")))
        .andReturn();
    json = result.getResponse().getContentAsString();

    LOG.debug("JSON = {}", json);
  }
}