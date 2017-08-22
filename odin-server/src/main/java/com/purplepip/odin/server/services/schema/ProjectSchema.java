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
import com.fasterxml.jackson.module.jsonSchema.customProperties.TitleSchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
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
  private Map<String, JsonSchema> flows = new HashMap<>();
  private Map<String, JsonSchema> foundations = new HashMap<>();

  /**
   * Create the project schema.
   */
  public ProjectSchema() {
    ObjectMapper mapper = new ObjectMapper();
    //SchemaFactoryWrapper visitor = new HyperSchemaFactoryWrapper();
    SchemaFactoryWrapper visitor = new TitleSchemaFactoryWrapper();

    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper, visitor);
    try {
      foundations.put("note", schemaGenerator.generateSchema(Note.class));
      foundations.put("tick", schemaGenerator.generateSchema(Tick.class));
      foundations.put("channel", schemaGenerator.generateSchema(Channel.class));
      foundations.put("layer", schemaGenerator.generateSchema(Layer.class));
    } catch (JsonMappingException e) {
      LOG.error("Could not load schema for core objects", e);
    }
    factory.getSequenceNames().forEach(name -> {
      try {
        JsonSchema schema = schemaGenerator.generateSchema(factory.getSequenceClass(name));
        flows.put(name, schema);
      } catch (JsonMappingException e) {
        LOG.error("Could not load schema for flow " + name, e);
      }
    });
  }

  public Stream<String> getFlowNames() {
    return flows.keySet().stream();
  }

  public Map<String, JsonSchema> getFlows() {
    return flows;
  }

  public JsonSchema getFlowSchema(String name) {
    return flows.get(name);
  }

  public Stream<String> getFoundationName() {
    return foundations.keySet().stream();
  }

  public Map<String, JsonSchema> getFoundations() {
    return foundations;
  }

  public JsonSchema getFoundationSchema(String name) {
    return flows.get(name);
  }
}
