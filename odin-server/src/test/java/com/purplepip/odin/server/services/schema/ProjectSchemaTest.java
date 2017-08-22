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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Project repository test.
 */
@Slf4j
public class ProjectSchemaTest {
  @Test
  public void testProjectSchema() {
    ProjectSchema schema = new ProjectSchema();
    assertEquals(3, schema.getFlowNames().count());
    JsonSchema notationSchema = schema.getFlowSchema("notation");
    logObjectAsJson(notationSchema);
    JsonSchema notationFormatSchema =
        notationSchema.asObjectSchema().getProperties().get("format");
    assertNotNull(notationFormatSchema);
    logObjectAsJson(notationFormatSchema);
    //Boolean readOnly = notationFormatSchema.getReadonly();
    //assertNotNull("Readonly flag should be set", readOnly);
    //assertTrue(readOnly);
  }

  private void logObjectAsJson(JsonSchema object) {
    if (LOG.isInfoEnabled()) {
      try {
        LOG.info("Schema : {}",
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
      } catch (JsonProcessingException e) {
        LOG.error("Cannot log schema as string");
      }
    }

  }
}