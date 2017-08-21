package com.purplepip.odin.server.rest;

import static com.purplepip.odin.server.rest.Rests.sendingJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@ActiveProfiles({"test", "noAuditing"})
@Slf4j
public class NotationRestTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testCreateAndDeleteSequence() throws Exception {
    String projectUri = new Rest(mvc).getFirstHref("project");

    /*
     * Add Sequence
     */
    String entityUri = mvc
        .perform(sendingJson(post("/api/notation")).content(
            new Json(objectMapper)
                .property("name", "new-notations-name")
                .property("project", projectUri)
                .property("notation", "A B C D")
                .property("flowName", "notation")
                .property("format", "natural")
                .toString()
        ))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse().getRedirectedUrl();

    /*
     * Check entity has been created
     */
    mvc.perform(sendingJson(get(projectUri + "/sequences")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.notation", hasSize(1)));

    /*
     * Get the entity
     */

    mvc.perform(sendingJson(get(entityUri)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.offset", is(0)))
        .andExpect(jsonPath("$.notation", is("A B C D")));

    /*
     * Delete entity
     */
    mvc.perform(sendingJson(delete(entityUri)))
        .andExpect(status().isNoContent());
  }
}