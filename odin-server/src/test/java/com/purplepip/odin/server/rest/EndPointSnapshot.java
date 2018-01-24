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

import static com.purplepip.odin.server.common.PrettyJson.toPrettyJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.purplepip.odin.snapshot.Snapshot;
import org.springframework.test.web.servlet.MockMvc;

public class EndPointSnapshot {
  private MockMvc mvc;
  private String uri;

  public EndPointSnapshot(MockMvc mvc, String uri) {
    this.mvc = mvc;
    this.uri = uri;
  }

  /**
   * Expect match of end point response with snapshot file.
   *
   * @throws Exception exception
   */
  public void expectMatch() throws Exception {
    String accept = uri.contains("profile") ? "application/schema+json" : "application/json";

    String json = mvc.perform(get(uri).accept(accept))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    Snapshot snapshot = new Snapshot(SequenceRestTest.class)
        .root("src/test/js")
        .path("data/" + uri)
        .extension("json").header(false).initialise();
    snapshot.writeLine(toPrettyJson(json));
    snapshot.expectMatch();
  }
}
