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

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.ArrayVisitor;
import com.fasterxml.jackson.module.jsonSchema.factories.ObjectVisitor;
import com.fasterxml.jackson.module.jsonSchema.factories.ObjectVisitorDecorator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.fasterxml.jackson.module.jsonSchema.validation.AnnotationConstraintResolver;
import com.fasterxml.jackson.module.jsonSchema.validation.ValidationConstraintResolver;
import com.purplepip.odin.common.OdinRuntimeException;
import lombok.extern.slf4j.Slf4j;

/**
 * Merge of ValidationSchemaFactoryWrapper and TitleSchemaFactoryWrapper since couldn't
 * find a way to combine more elegantly.
 */
@Slf4j
public class OdinSchemaFactoryWrapper extends SchemaFactoryWrapper {
  private ValidationConstraintResolver constraintResolver;

  private static class OdinSchemaFactoryWrapperFactory extends WrapperFactory {

    @Override
    public SchemaFactoryWrapper getWrapper(SerializerProvider provider) {
      SchemaFactoryWrapper wrapper = new OdinSchemaFactoryWrapper();
      wrapper.setProvider(provider);
      return wrapper;
    }

    @Override
    public SchemaFactoryWrapper getWrapper(SerializerProvider provider,
                                           VisitorContext visitorContext) {
      SchemaFactoryWrapper wrapper = new OdinSchemaFactoryWrapper();
      wrapper.setProvider(provider);
      wrapper.setVisitorContext(visitorContext);
      return wrapper;
    }
  }

  private static class WrapperAwareObjectVisitorDecorator extends ObjectVisitorDecorator {
    private OdinSchemaFactoryWrapper wrapper;

    public WrapperAwareObjectVisitorDecorator(ObjectVisitor objectVisitor,
                                  OdinSchemaFactoryWrapper wrapper) {
      super(objectVisitor);
      this.wrapper = wrapper;
    }

    private JsonSchema getPropertySchema(BeanProperty property) {
      return ((ObjectSchema) getSchema()).getProperties().get(property.getName());
    }

    @Override
    public void optionalProperty(BeanProperty property) throws JsonMappingException {
      super.optionalProperty(property);
      wrapper.addReadOnly(getPropertySchema(property), property);
      wrapper.addValidationConstraints(getPropertySchema(property), property);
    }

    @Override
    public void property(BeanProperty property) throws JsonMappingException {
      super.property(property);
      wrapper.addReadOnly(getPropertySchema(property), property);
      wrapper.addValidationConstraints(getPropertySchema(property), property);
    }
  }

  public OdinSchemaFactoryWrapper() {
    this(new AnnotationConstraintResolver());
  }

  public OdinSchemaFactoryWrapper(ValidationConstraintResolver constraintResolver) {
    super(new OdinSchemaFactoryWrapperFactory());
    this.constraintResolver = constraintResolver;
  }

  @Override
  public JsonObjectFormatVisitor expectObjectFormat(JavaType convertedType) {
    ObjectVisitor visitor = ((ObjectVisitor) super.expectObjectFormat(convertedType));
    addTitle(visitor.getSchema(), convertedType);
    return new WrapperAwareObjectVisitorDecorator(visitor, this);
  }

  @Override
  public JsonArrayFormatVisitor expectArrayFormat(JavaType convertedType) {
    ArrayVisitor visitor = ((ArrayVisitor) super.expectArrayFormat(convertedType));
    addTitle(visitor.getSchema(), convertedType);
    return visitor;
  }

  private void addTitle(JsonSchema schema, JavaType type) {
    if (!schema.isSimpleTypeSchema()) {
      throw new OdinRuntimeException("Not simple type schema " + schema.getType());
    }
    schema.asSimpleTypeSchema().setTitle(type.getRawClass().getSimpleName());
  }

  protected void addReadOnly(JsonSchema schema, BeanProperty property) {
    JsonProperty jsonProperty = property.getAnnotation(JsonProperty.class);
    Boolean readOnly = jsonProperty != null && jsonProperty.access() == READ_ONLY;
    schema.setReadonly(readOnly);
    LOG.debug("TODO : implement readonly");
  }


  protected JsonSchema addValidationConstraints(JsonSchema schema, BeanProperty property) {
    Boolean required = constraintResolver.getRequired(property);
    if (required != null) {
      schema.setRequired(required);
    }
    if (schema.isArraySchema()) {
      ArraySchema arraySchema = schema.asArraySchema();
      arraySchema.setMaxItems(constraintResolver.getArrayMaxItems(property));
      arraySchema.setMinItems(constraintResolver.getArrayMinItems(property));
    } else if (schema.isNumberSchema()) {
      NumberSchema numberSchema = schema.asNumberSchema();
      numberSchema.setMaximum(constraintResolver.getNumberMaximum(property));
      numberSchema.setMinimum(constraintResolver.getNumberMinimum(property));
    } else if (schema.isStringSchema()) {
      StringSchema stringSchema = schema.asStringSchema();
      stringSchema.setMaxLength(constraintResolver.getStringMaxLength(property));
      stringSchema.setMinLength(constraintResolver.getStringMinLength(property));
      stringSchema.setPattern(constraintResolver.getStringPattern(property));
    }
    return schema;
  }
}
