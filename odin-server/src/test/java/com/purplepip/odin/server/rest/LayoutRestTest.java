package com.purplepip.odin.server.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
import java.util.Map;
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
public class LayoutRestTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testCreateNewLayer() throws Exception {
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get(
        "/api/projects"))
        .andExpect(status().isOk())
        .andReturn();
    DocumentContext context = JsonPath.parse(result.getResponse().getContentAsString());
    String projectUri = context.read("$._embedded.projects[0]._links.self.href");
    Map<String, String> layer = new HashMap<>();
    layer.put("name", "new-layer-name");
    layer.put("project", projectUri);
    String newLayerJson = objectMapper.writeValueAsString(layer);
    LOG.info(newLayerJson);
    result = mvc.perform(MockMvcRequestBuilders.post(
          "/api/layers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(newLayerJson))
        .andExpect(status().isCreated())
        .andReturn();
    String json = result.getResponse().getContentAsString();
    LOG.info("JSON = {}", json);
  }
}