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
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Project repository test.
 */
@Slf4j
public class ProjectSchemaTest {
  @Test
  public void testCore() {
    ProjectSchema project = new ProjectSchema();
    JsonSchema schema = project.getType("urn:jsonschema:com:purplepip:odin:math:Rational");
    Map<String, JsonSchema> properties = schema.asObjectSchema().getProperties();
    Boolean readOnly = properties.get("simplified").asBooleanSchema().getReadonly();
    assertNotNull("Read only should be set", readOnly);
  }

  @Test
  public void testFlows() {
    ProjectSchema schema = new ProjectSchema();
    logObjectAsJson(schema);
    assertEquals(3, schema.getFlowNames().size());
    JsonSchema notationSchema = schema.getFlowSchema("notation");
    assertEquals("Notation", notationSchema.asObjectSchema().getTitle());
    logObjectAsJson(notationSchema);
    Map<String, JsonSchema> properties = notationSchema.asObjectSchema().getProperties();
    assertEquals("string", properties.get("format").getType().value());
    assertEquals(Double.valueOf(-1.0), properties.get("length").asNumberSchema().getMinimum());
  }

  private void logObjectAsJson(Object object) {
    if (LOG.isInfoEnabled()) {
      try {
        LOG.info("Schema : {}",
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
      } catch (JsonProcessingException e) {
        LOG.error("Cannot log schema as string", e);
      }
    }
  }
}