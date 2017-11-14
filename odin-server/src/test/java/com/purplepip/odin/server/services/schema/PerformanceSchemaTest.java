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

package com.purplepip.odin.server.services.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Performance repository test.
 */
@Slf4j
public class PerformanceSchemaTest {
  @Test
  public void testPerformance() {
    PerformanceSchema performance = new PerformanceSchema();
    JsonNode schema = performance.getType(
        "urn:jsonschema:com:purplepip:odin:performance:Performance");
    JsonNode properties = schema.get("properties");
    String type = properties.get("layers").get("type").asText();
    assertEquals("array", type);
    JsonNode sequenceDefinition = schema.get("definitions").get("SequenceConfiguration");
    assertNotNull("Sequence definition not provided in " + schema.get("definitions"),
        sequenceDefinition);
    JsonNode lengthType = sequenceDefinition.get("properties").get("length").get("type");
    assertNotNull("length definition not correct in " + sequenceDefinition, lengthType);
    assertEquals("integer", lengthType.asText());
  }

  @Test
  public void testFlows() {
    PerformanceSchema schema = new PerformanceSchema();
    logObjectAsJson(schema);
    assertEquals(4, schema.getFlows().size());
    JsonNode notationSchema = schema.getFlowSchema("notation");
    assertEquals("Notation", notationSchema.get("title").asText());
    logObjectAsJson(notationSchema);
    JsonNode properties = notationSchema.get("properties");
    assertEquals("string", properties.get("format").get("type").asText());
  }

  private void logObjectAsJson(Object object) {
    if (LOG.isInfoEnabled()) {
      try {
        LOG.debug("Schema : {}",
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
      } catch (JsonProcessingException e) {
        LOG.error("Cannot log schema as string", e);
      }
    }
  }
}