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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequencer.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectSchema {
  private SequenceFactory factory = new SequenceFactory();
  private Map<String, JsonSchema> types = new HashMap<>();
  private Map<String, String> flows = new HashMap<>();

  /**
   * Create the project schema.
   */
  public ProjectSchema() {
    ObjectMapper mapper = new ObjectMapper();
    OdinSchemaFactoryWrapper visitor = new OdinSchemaFactoryWrapper();

    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper, visitor);
    try {
      registerType(schemaGenerator.generateSchema(Real.class));
      registerType(schemaGenerator.generateSchema(Rational.class));
      registerType(schemaGenerator.generateSchema(Whole.class));
      registerType(schemaGenerator.generateSchema(Note.class));
      registerType(schemaGenerator.generateSchema(Tick.class));
      registerType(schemaGenerator.generateSchema(Channel.class));
      registerType(schemaGenerator.generateSchema(Layer.class));
    } catch (JsonMappingException e) {
      LOG.error("Could not load schema for core objects", e);
    }
    factory.getSequenceNames().forEach(name -> {
      try {
        registerFlow(name, schemaGenerator.generateSchema(factory.getSequenceClass(name)));
      } catch (JsonMappingException e) {
        LOG.error("Could not load schema for flow " + name, e);
      }
    });
  }

  private void registerType(JsonSchema schema) {
    types.put(schema.getId(), schema);
  }

  private void registerFlow(String flowName, JsonSchema schema) {
    registerType(schema);
    flows.put(flowName, schema.getId());
  }

  public Stream<String> getFlowNames() {
    return flows.keySet().stream();
  }

  public Map<String, JsonSchema> getTypes() {
    return types;
  }

  public JsonSchema getFlowSchema(String name) {
    return types.get(flows.get(name));
  }

  public Stream<String> getTypeNames() {
    return types.keySet().stream();
  }

  public JsonSchema getType(String name) {
    return types.get(name);
  }
}
