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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequencer.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectSchema {
  private SequenceFactory factory = new SequenceFactory();
  private Map<String, JsonNode> types = new HashMap<>();
  private Map<String, String> flows = new HashMap<>();

  /**
   * Create the project schema.
   */
  public ProjectSchema() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper);
    registerType(schemaGenerator, Real.class);
    registerType(schemaGenerator, Rational.class);
    registerType(schemaGenerator, Whole.class);
    registerType(schemaGenerator, Note.class);
    registerType(schemaGenerator, Tick.class);
    registerType(schemaGenerator, Project.class);
    registerType(schemaGenerator, Sequence.class);
    registerType(schemaGenerator, Channel.class);
    registerType(schemaGenerator, Layer.class);
    /*
     * Re-register project so that it now references types instead of inlining them.
     */
    registerType(schemaGenerator, Project.class);
    factory.getSequenceNames().forEach(name -> {
      registerFlow(schemaGenerator, name, factory.getSequenceClass(name));
    });
  }

  private void registerType(JsonSchemaGenerator schemaGenerator, Class<?> clazz) {
    JsonNode schema = schemaGenerator.generateJsonSchema(clazz);
    types.put(getSchemaReference(clazz), schema);
  }

  private String getSchemaReference(Class clazz) {
    return "urn:jsonschema:" + clazz.getName().replace('.',':');
  }

  private void registerFlow(JsonSchemaGenerator schemaGenerator, String flowName, Class clazz) {
    registerType(schemaGenerator, clazz);
    flows.put(flowName, getSchemaReference(clazz));
  }

  public Map<String, String> getFlows() {
    return flows;
  }

  public Map<String, JsonNode> getTypes() {
    return types;
  }

  public String getFlowTypeRef(String name) {
    return flows.get(name);
  }

  public JsonNode getFlowSchema(String name) {
    return types.get(getFlowTypeRef(name));
  }

  public Set<String> getTypeRefs() {
    return types.keySet();
  }

  public JsonNode getType(String name) {
    return types.get(name);
  }
}
