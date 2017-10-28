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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
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

  public Class<? extends C> getClass(String name) {
    return specificClasses.get(name);
  }

  public Stream<String> getNames() {
    return specificClasses.keySet().stream();
  }
}
