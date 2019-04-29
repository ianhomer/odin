package com.purplepip.odin.api.services.schema;

import static com.purplepip.odin.api.common.PrettyJson.toPrettyJson;
import static com.purplepip.odin.api.rest.Rests.sendingJson;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.purplepip.odin.snapshot.Snapshot;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "noStore", "noAuditing"})
@Slf4j
class SchemaControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void testFlowSchema() throws Exception {
    String json = mvc.perform(sendingJson(get("/api/services/schema/flows/notation")))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    LOG.debug("JSON = {}", json);
  }

  @Test
  void testFullSchema() throws Exception {
    String json = mvc.perform(sendingJson(get("/api/services/schema")))
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

    Snapshot snapshot = new Snapshot(SchemaController.class)
        .root("src/test/resources")
        .path("com/purplepip/odin/api/data/api/services/schema")
        .extension("json").header(false).initialise();
    snapshot.writeLine(toPrettyJson(json));
    snapshot.expectMatch();
  }
}