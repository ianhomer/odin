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

import static com.purplepip.odin.math.typeconverters.MathTypeConverterManager.requireMathTypeConverters;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.properties.thing.ThingCopy;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory that allow the generation of instances for specific classes from generic configuration
 * instances. This allows generic configuration objects to be serialised and persisted which then
 * can be instantiated in the run time as a specific class of the object with appropriate business
 * logic attached. The type property on the the thing configuration interface allows this lookup of
 * the specific class that should be used for instantiation of a new object.
 *
 * @param <C> specific class base type
 */
@Slf4j
public abstract class AbstractSpecificThingFactory<C extends ThingConfiguration> {
  static {
    requireMathTypeConverters();
  }

  private final Map<String, Class<? extends C>> specificClasses = new HashMap<>();

  protected AbstractSpecificThingFactory(List<Class<? extends C>> classes) {
    for (Class<? extends C> clazz : classes) {
      register(clazz);
    }
    warmUp();
  }

  private void register(Class<? extends C> clazz) {
    if (clazz.isAnnotationPresent(Name.class)) {
      put(clazz.getAnnotation(Name.class).value(), clazz);
    } else {
      Annotation[] annotations = clazz.getAnnotations();
      LOG.warn("Class {} MUST have a @Name annotation, it has {}", clazz, annotations);
    }
  }

  private void put(String name, Class<? extends C> clazz) {
    specificClasses.put(name, clazz);
  }

  /**
   * Create a new instance of the sequence configuration and cast to the required sequence type.
   *
   * @param configuration for the new instance
   */
  public C newInstance(ThingConfiguration configuration) {
    return newInstance(configuration, getClass(configuration.getType()));
  }

  /**
   * Create an instance of the expected type using the given configuration object.
   *
   * @param expectedType expected type of the returned instance
   * @param original original configuration to copy values from
   * @return instance of expected type
   */
  @SuppressWarnings("unchecked")
  public <S extends C> S newInstance(ThingConfiguration original, Class<? extends S> expectedType) {
    S newInstance;
    if (expectedType == null) {
      throw new OdinRuntimeException(
          "Thing configuration type for original configuration "
              + original
              + " is not set.  Type is "
              + original.getType()
              + " ; registered types = "
              + specificClasses);
    } else {
      if (expectedType.isAssignableFrom(original.getClass())) {
        /*
         * If the original is of the correct type then we can simply cast
         */
        // TODO : Since we cast not copy then the name of the method newInstance is misleading
        newInstance = expectedType.cast(original);
        LOG.debug("Creating new instance with cast of {}", newInstance);
      } else {
        LOG.trace("Creating new instance of {}", expectedType);
        try {
          newInstance = expectedType.getConstructor().newInstance();
        } catch (InstantiationException
            | IllegalAccessException
            | NoSuchMethodException
            | InvocationTargetException e) {
          throw new OdinRuntimeException("Cannot create new instance of " + expectedType, e);
        }
        populate(newInstance, original);
        LOG.debug("Creating instance with typed copy {}", newInstance);
      }
    }
    return newInstance;
  }

  @SuppressWarnings("unchecked")
  public C cast(ThingConfiguration configuration) {
    return (C) configuration;
  }

  protected void populate(C destination, ThingConfiguration source) {
    new ThingCopy().source(source).destination(destination).copy();
  }

  public Class<? extends C> getClass(String name) {
    return specificClasses.get(name);
  }

  public Stream<String> getNames() {
    return specificClasses.keySet().stream();
  }

  /**
   * For test cases where timing is important it may be necessary to warm the factory up so the
   * first time it is used performance does not cause inconsistencies. This warm up time is pretty
   * small (around 20ms on a dev machine), but enough to throw a sequencer test that is expecting
   * sequence to start immediately.
   */
  private void warmUp() {
    LOG.debug("Warming up {}", this);
    getNames()
        .forEach(
            name -> {
              MutableThingConfiguration thingConfiguration = new GenericThingConfiguration(name);
              newInstance(thingConfiguration);
            });
  }
}
