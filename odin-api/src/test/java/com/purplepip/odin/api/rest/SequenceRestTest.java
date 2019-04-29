package com.purplepip.odin.api.rest;

import static com.purplepip.odin.api.rest.Rests.sendingJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@ActiveProfiles({"test", "noAuditing"})
@Slf4j
public class SequenceRestTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testCreateAndDeleteSequence() throws Exception {
    String performanceUri = new Rest(mvc).getFirstHref("performance");

    assertNoSequences(performanceUri);

    /*
     * Add Sequence
     */
    String entityUri = mvc
        .perform(sendingJson(post("/api/rest/sequence")).content(
            new Json(objectMapper)
                .property("name", "new-notations-name")
                .property("performance", performanceUri)
                .property("type", "notation")
                .property("offset", "3/2")
                .property("length", "2")
                .properties()
                .property("notation", "A B C D")
                .property("format", "natural")
                .toString()
        ))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse().getRedirectedUrl();

    assertNotNull(entityUri);

    /*
     * Check entity has been created
     */
    assertSequenceSize(performanceUri, 1);

    /*
     * Get the entity
     */

    mvc.perform(sendingJson(get(entityUri)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.offset", is("1½")))
        .andExpect(jsonPath("$.length", is("2")))
        .andExpect(jsonPath("$.properties.notation", is("A B C D")));

    /*
     * Delete entity
     */
    mvc.perform(sendingJson(delete(entityUri)))
        .andExpect(status().isNoContent());

    /*
     * Check entity has been removed
     */
    assertNoSequences(performanceUri);
  }

  private void assertSequenceSize(String performanceUri, int expected) throws Exception {
    mvc.perform(sendingJson(get(performanceUri + "/sequences")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.sequence", hasSize(expected)));
  }

  private void assertNoSequences(String performanceUri) throws Exception {
    assertSequenceSize(performanceUri,0);
  }


  @Test
  void testProfile() throws Exception {
    new EndPointSnapshot(mvc, "/api/rest/profile/sequence").expectMatch();
  }
}