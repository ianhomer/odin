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

package com.purplepip.odin.server.services.system;

import static com.purplepip.odin.server.common.PrettyJson.toPrettyJson;
import static com.purplepip.odin.server.rest.Rests.sendingJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.purplepip.odin.snapshot.Snapshot;
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
public class EnvironmentControllerTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void testEnvironment() throws Exception {
    String json = mvc.perform(sendingJson(get("/api/services/system/environment")))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    Snapshot snapshot = new Snapshot(EnvironmentController.class)
        .root("src/test/js")
        .path("data/api/services/system/environment")
        .extension("json").header(false).initialise();

    snapshot.writeLine(toPrettyJson(json));
    snapshot.expectMatch();
  }
}
