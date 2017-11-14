/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.server.rest;

import static com.purplepip.odin.server.rest.Rests.createHrefJsonPath;
import static com.purplepip.odin.server.rest.Rests.sendingJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.web.servlet.MockMvc;

/**
 * REST utilities for test cases to simplify boiler plate code.
 */
@Slf4j
public class Rest {
  private MockMvc mvc;

  public Rest(MockMvc mvc) {
    this.mvc = mvc;
  }

  /**
   * Get the href of the first entity in the given collection.
   *
   * @param entitiesName entities name, e.g. performances
   * @return href for the first entity
   * @throws Exception exception
   */
  public String getFirstHref(String entitiesName) throws Exception {
    return getHref(entitiesName, 0);
  }

  /**
   * Get the href of the entity with the given index in the given collection.
   *
   * @param entitiesName entities name, e.g. performances
   * @return href for the entity with the given index
   * @throws Exception exception
   */
  public String getHref(String entitiesName, int index) throws Exception {
    String href = JsonPath.parse(
        mvc.perform(sendingJson(get("/api/" + entitiesName)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse().getContentAsString()
    ).read(createHrefJsonPath(entitiesName, index));
    LOG.debug("href for {}[{}] = {}", entitiesName, index, href);
    return href;
  }
}
