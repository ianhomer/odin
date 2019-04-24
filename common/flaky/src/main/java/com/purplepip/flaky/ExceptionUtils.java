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

package com.purplepip.flaky;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtils {
  private static final Logger LOG = LoggerFactory.getLogger(ExceptionUtils.class);

  static String extractMessage(Throwable throwable) {
    return Stream.of(throwable.getStackTrace())
        .filter(element -> {
          try {
            return Stream.of(Class.forName(element.getClassName()).getDeclaredMethods())
                .filter(method -> element.getMethodName().equals(method.getName()))
                .anyMatch(method -> findAnnotation(method, FlakyTest.class).isPresent());
          } catch (ClassNotFoundException e) {
            LOG.error("Cannot load class", e);
            return false;
          }
        }).findFirst().map(element ->
          String.format("%s.%s:%s", getSimpleClassName(element.getClassName()),
              element.getMethodName(), element.getLineNumber())
        ).orElse("unknown");
  }

  private static String getSimpleClassName(String className) {
    try {
      return Class.forName(className).getSimpleName();
    } catch (ClassNotFoundException e) {
      return className;
    }
  }
}
