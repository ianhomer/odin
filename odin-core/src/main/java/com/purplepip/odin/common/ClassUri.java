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

import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

/**
 * Generate a classpath URI for a given class.
 */
@Slf4j
public class ClassUri {
  private static final String SCHEME = "classpath";
  private final Class clazz;
  private final URI uri;

  public ClassUri(Class clazz) {
    this(clazz, false);
  }

  /**
   * Create a new class URI object.
   *
   * @param clazz Class to base the class URI object on
   * @param withScheme whether the URI includes the scheme part.  If scheme is set on the URI then
   *                   the resource MUST be located on the classpath, otherwise it is allowed to
   *                   be located according to any supported scheme.
   */
  public ClassUri(Class clazz, boolean withScheme) {
    this.clazz = clazz;
    try {
      uri = new URI(withScheme ? SCHEME : null,
          clazz.getName().replace('.', '/'), null);
    } catch (URISyntaxException e) {
      throw new OdinRuntimeException("Cannot create URI for " + clazz, e);
    }
  }

  public Class getClazz() {
    return clazz;
  }

  public URI getUri() {
    return uri;
  }

  public String toString() {
    return uri.toString();
  }
}
