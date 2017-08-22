package com.purplepip.odin.server.services.schema;

import static com.purplepip.odin.server.rest.Rests.sendingJson;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    LOG.info("JSON = {}", json);
  }

  @Test
  public void testFullSchema() throws Exception {
    String json = mvc.perform(sendingJson(get("/services/schema")))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(
                "urn:jsonschema:com:purplepip:odin:music:sequence:Pattern")))
        .andExpect(jsonPath("$.flows.notation.type", is("object")))
        .andExpect(jsonPath("$.flows.notation.properties.notation.type",
            is("string")))
        .andExpect(jsonPath("$.flows.notation.properties.offset.type",
            is("integer")))
        .andReturn().getResponse().getContentAsString();
    LOG.info("JSON = {}", json);
  }

}