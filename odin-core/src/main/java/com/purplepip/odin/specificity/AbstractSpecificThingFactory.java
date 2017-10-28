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

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import com.purplepip.odin.properties.beany.PropertiesProvider;
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
 * instance.
 *
 * @param <C> specific class base type
 */
@Slf4j
public class AbstractSpecificThingFactory<C> {
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

  protected void populate(MutablePropertiesProvider destination,
                          PropertiesProvider source) {
    LOG.debug("Copying original object properties to copy");
    BeanCopy.from(source).to(destination).copy();
    LOG.debug("Copying original properties map to copy");
    source.getPropertyNames()
        .forEach(name -> {
          destination.setProperty(name, source.getProperty(name));
          try {
            BeanUtil.declared.setProperty(destination, name, source.getProperty(name));
          } catch (BeanException e) {
            LOG.debug("Whilst populating thing {} (full stack)", e);
            LOG.warn("Whilst populating thing {}", e.getMessage());
          }
        });
  }

  public Class<? extends C> getClass(String name) {
    return specificClasses.get(name);
  }

  public Stream<String> getNames() {
    return specificClasses.keySet().stream();
  }
}
