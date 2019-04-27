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

import static org.junit.Assert.assertEquals;

import java.net.URI;
import org.junit.jupiter.api.Test;

public class ClassUriTest {
  @Test
  public void getUri() throws Exception {
    ClassUri classUri = new ClassUri(ClassUri.class);
    assertEquals(new URI("com/purplepip/odin/common/ClassUri"),
        classUri.getUri());
    assertEquals("com/purplepip/odin/common/ClassUri", classUri.toString());
    classUri = new ClassUri(ClassUri.class, true);
    assertEquals("classpath:com/purplepip/odin/common/ClassUri", classUri.toString());
  }
}