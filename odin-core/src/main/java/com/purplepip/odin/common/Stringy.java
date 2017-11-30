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

package com.purplepip.odin.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenience utility for object toString.
 */
public class Stringy {
  private static final char OPEN = '(';
  private static final char CLOSE = ')';

  private Class clazz;
  private Map<String, String> nameValues = new LinkedHashMap<>();
  private boolean includeNulls = false;

  public static Stringy of(Class clazz) {
    return new Stringy(clazz);
  }

  private Stringy(Class clazz) {
    this.clazz = clazz;
  }

  public Stringy includeNulls() {
    includeNulls = true;
    return this;
  }

  /**
   * Add entries stream to stringy.
   *
   * @param name property name
   * @param entries entries stream
   * @return this
   */
  public Stringy add(String name, Stream<Map.Entry<String, String>> entries) {
    String entriesAsString = toString(entries);
    if (entriesAsString.length() > 0 || includeNulls) {
      put(name, "[" + entriesAsString + "]");
    }
    return this;
  }

  /**
   * Add name value.
   *
   * @param name name
   * @param value value
   * @param predicate predicate
   * @return this
   */
  public Stringy add(String name, Object value, Predicate<Object> predicate) {
    if (value != null && predicate.test(value)) {
      add(name, value);
    }
    return this;
  }

  /**
   * Add value to stringy.
   *
   * @param name property name
   * @param value value
   * @return this
   */
  public Stringy add(String name, Object value) {
    if (value == null) {
      if (includeNulls) {
        put(name, null);
      }
    } else {
      put(name, value.toString());
    }
    return this;
  }

  private void put(String name, String value) {
    nameValues.put(name, value);
  }

  public String build() {
    return clazz.getSimpleName() + OPEN + toString(nameValues) + CLOSE;
  }

  private String toString(Map<String, String> map) {
    return toString(map.entrySet().stream());
  }

  private String toString(Stream<Map.Entry<String, String>> entries) {
    return entries
        .filter(entry -> entry.getValue() != null || includeNulls)
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining(", "));
  }
}
