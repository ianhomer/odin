/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

import static com.purplepip.odin.server.rest.Rests.sendingJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MockMvc;

/**
 * REST utilities for test cases to simplify boiler plate code
 */
public class Rest {
  private MockMvc mvc;

  public Rest(MockMvc mvc) {
    this.mvc = mvc;
  }

  public String getFirstProjectUri() throws Exception {
    return JsonPath.parse(
        mvc.perform(sendingJson(get("/api/projects")))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse().getContentAsString()
    ).read("$._embedded.projects[0]._links.self.href");
  }
}
