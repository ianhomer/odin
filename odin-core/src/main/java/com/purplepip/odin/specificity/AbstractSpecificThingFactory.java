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

package com.purplepip.odin.specificity;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import com.purplepip.odin.sequence.flow.RationalTypeConverter;
import com.purplepip.odin.sequence.flow.RealTypeConverter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import jodd.bean.BeanCopy;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import jodd.typeconverter.TypeConverterManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory that allow the generation of instances for specific classes from generic configuration
 * instances.
 *
 * @param <C> specific class base type
 */
@Slf4j
public class AbstractSpecificThingFactory<C extends ThingConfiguration> {
  private final Map<String, Class<? extends C>> specificClasses = new HashMap<>();

  static {
    TypeConverterManager.register(Real.class, new RealTypeConverter());
    TypeConverterManager.register(Rational.class, new RationalTypeConverter());
  }

  protected AbstractSpecificThingFactory(List<Class<? extends C>> classes) {
    for (Class<? extends C> clazz: classes) {
      register(clazz);
    }
  }

  protected void register(Class<? extends C> clazz) {
    if (clazz.isAnnotationPresent(Name.class)) {
      Name definition = clazz.getAnnotation(Name.class);
      put(definition.value(), clazz);
    } else {
      Annotation[] annotations = clazz.getAnnotations();
      LOG.warn("Class {} MUST have a @Name annotation, it has {}", clazz, annotations);
    }
  }

  protected void put(String name, Class<? extends C> clazz) {
    specificClasses.put(name, clazz);
  }

  /**
   * Create a copy of the sequence with the expected type.
   *
   * @param expectedType expected type of the returned sequence
   * @param original     original sequence to copy values from
   * @return sequence of expected type
   */
  @SuppressWarnings("unchecked")
  public <S extends C> S newInstance(ThingConfiguration original,
                                        Class<? extends S> expectedType) {
    S newInstance;
    if (expectedType == null) {
      throw new OdinRuntimeException("Expected sequence type for " + original + " is not set");
    } else {
      if (expectedType.isAssignableFrom(original.getClass())) {
        /*
         * If the original is of the correct type then we can simply take a copy
         */
        newInstance = (S) expectedType.cast(original).copy();
        LOG.debug("Starting flow with sequence copy {}", newInstance);
      } else {
        LOG.debug("Creating new instance of {}", expectedType);
        try {
          newInstance = expectedType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new OdinRuntimeException("Cannot create new instance of " + expectedType, e);
        }
        populate(newInstance, original);
        newInstance.afterPropertiesSet();
        LOG.debug("Starting flow with typed copy {}", newInstance);
      }
    }
    return newInstance;
  }

  protected void populate(C destination, ThingConfiguration source) {
    LOG.debug("Populating bean properties from source");
    BeanCopy.from(source).to(destination).copy();
    LOG.debug("Populating properties map from source");
    if (destination instanceof MutablePropertiesProvider) {
      source.getPropertyNames().forEach(name -> {
        ((MutablePropertiesProvider) destination).setProperty(name, source.getProperty(name));
        try {
          BeanUtil.declared.setProperty(destination, name, source.getProperty(name));
        } catch (BeanException e) {
          LOG.debug("Whilst populating thing {} (full stack)", e);
          LOG.warn("Whilst populating thing {}", e.getMessage());
        }
      });
    }
  }

  public Class<? extends C> getClass(String name) {
    return specificClasses.get(name);
  }

  public Stream<String> getNames() {
    return specificClasses.keySet().stream();
  }
}
