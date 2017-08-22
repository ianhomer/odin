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

package com.purplepip.odin.server.services.schema;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!noServices")
@Slf4j
public class SchemaController {
  @Autowired
  private ProjectSchema projectSchema;

  /**
   * Get full project schema.
   *
   * @return project json schema
   */
  @RequestMapping("/services/schema")
  public ProjectSchema getProjectSchema() {
    return projectSchema;
  }

  /**
   * Get schema for given sequence name.
   *
   * @return flow json schema
   */
  @RequestMapping("/services/schema/flows/{name}")
  public JsonSchema getFlowSchema(@PathVariable("name") String name) {
    return projectSchema.getFlowSchema(name);
  }
}
