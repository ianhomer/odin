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

package com.purplepip.odin.api.services.schema;

import static com.purplepip.odin.configuration.SequenceFactories.newNoteSequenceFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.sequence.SequenceFactory;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.store.domain.PerformanceBoundSequence;
import com.purplepip.odin.store.domain.PersistableNote;
import com.purplepip.odin.store.domain.PersistableTick;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PerformanceSchema {
  private SequenceFactory factory = newNoteSequenceFactory();
  private Map<String, JsonNode> types = new HashMap<>();
  private Map<String, String> flows = new HashMap<>();

  /**
   * Create the performance schema.
   */
  public PerformanceSchema() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

    Map<String, String> customType2FormatMapping = new HashMap<>();
    customType2FormatMapping.put(Performance.class.getName(), "uri");

    Map<Class<?>, Class<?>> classTypeMapping = new HashMap<>();
    classTypeMapping.put(Performance.class, String.class);
    classTypeMapping.put(Tick.class, PersistableTick.class);
    classTypeMapping.put(Note.class, PersistableNote.class);
    classTypeMapping.put(SequenceConfiguration.class, PerformanceBoundSequence.class);

    /*
     * TODO : Treat rationals as better than integers
     */
    classTypeMapping.put(Rational.class, Integer.class);

    JsonSchemaConfig config = JsonSchemaConfig.create(
        true,
        Optional.empty(),
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        customType2FormatMapping,
        false,
        new HashSet<>(),
        classTypeMapping,
        new HashMap<>()
    );

    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper);
    /*
     * Re-register performance so that it now references types instead of inlining them.
     */
    registerType(schemaGenerator, Performance.class, getSchemaReference(Performance.class));

    JsonSchemaGenerator customSchemaGenerator = new JsonSchemaGenerator(mapper, config);

    registerType(customSchemaGenerator, SequenceConfiguration.class,
        getSchemaReference(SequenceConfiguration.class));

    /*
     * Register sequence flows performance referenced.
     */
    factory.getNames().forEach(name ->
        registerFlow(customSchemaGenerator, name, factory.getClass(name))
    );
  }

  private void registerType(JsonSchemaGenerator schemaGenerator, Class<?> clazz, String id) {
    JsonNode schema = schemaGenerator.generateJsonSchema(clazz);
    types.put(id, schema);
  }

  private static String getSchemaReference(Class clazz) {
    return "urn:jsonschema:" + clazz.getName().replace('.', ':');
  }

  private void registerFlow(JsonSchemaGenerator schemaGenerator, String typeName, Class clazz) {
    registerType(schemaGenerator, clazz, getFlowSchemaReference(clazz));
    flows.put(typeName, getFlowSchemaReference(clazz));
  }

  /*
   * TODO : AJV module in front end doesn't seem to work for referenced properties on IDs
   * in urn format, e.g. the following is not found
   * urn:jsonschema:com:purplepip:odin:music:sequence:Notation#/definitions/PersistableTick
   * ... not sure, when end to OK we should confirm if this urn is not valid according to
   * spec or if this is AJV limitation.
   */
  private static String getFlowSchemaReference(Class clazz) {
    return "flow-" + clazz.getSimpleName().toLowerCase(Locale.ENGLISH);
  }

  public Map<String, String> getFlows() {
    return flows;
  }

  public Map<String, JsonNode> getTypes() {
    return types;
  }

  public JsonNode getFlowSchema(String name) {
    return types.get(getFlowTypeRef(name));
  }

  public String getFlowTypeRef(String name) {
    return flows.get(name);
  }

  public Set<String> getTypeRefs() {
    return types.keySet();
  }

  public JsonNode getType(String name) {
    return types.get(name);
  }
}
