package com.purplepip.odin.server.services.schema;

import static com.purplepip.odin.server.rest.Rests.sendingJson;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@ActiveProfiles({"test", "noStore", "noAuditing"})
@Slf4j
public class SchemaControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testFlowSchema() throws Exception {
    String json = mvc.perform(sendingJson(get("/services/schema/flows/notation")))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    LOG.debug("JSON = {}", json);
  }

  @Test
  public void testFullSchema() throws Exception {
    String json = mvc.perform(sendingJson(get("/services/schema")))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(
                "flow-pattern")))
        .andExpect(jsonPath(
            "$.types.flow-notation.type",
            is("object")))
        .andExpect(jsonPath(
            "$.types.flow-notation"
                + ".properties.notation.type",
            is("string")))
        .andExpect(jsonPath(
            "$.types.flow-notation"
                + ".properties.offset.type",
            is("integer")))
        .andReturn().getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    LOG.debug("JSON = {}",
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(json)));
  }
}