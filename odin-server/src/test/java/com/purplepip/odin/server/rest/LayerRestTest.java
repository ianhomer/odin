package com.purplepip.odin.server.rest;

import static com.purplepip.odin.server.rest.Rests.sendingJson;
import static org.hamcrest.Matchers.hasSize;
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
public class LayerRestTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testCreateAndDeleteLayer() throws Exception {
    String performanceUri = new Rest(mvc).getFirstHref("performance");

    /*
     * Add layer
     */
    String entityUri = mvc
        .perform(sendingJson(post("/api/layer")).content(
            new Json(objectMapper)
                .property("name", "new-layer-name")
                .property("performance", performanceUri).toString()
        ))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse().getRedirectedUrl();

    /*
     * Check entity has been created
     */
    mvc.perform(sendingJson(get(performanceUri + "/layers")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.layer", hasSize(1)));

    /*
     * Delete entity
     */
    mvc.perform(sendingJson(delete(entityUri)))
        .andExpect(status().isNoContent());

    /*
     * Test performance has no layouts
     */
    mvc.perform(sendingJson(get(performanceUri + "/layers")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.layer", hasSize(0)));
  }
}