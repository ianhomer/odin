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

package com.purplepip.odin.music.sequence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.assertj.core.util.Lists;
import org.junit.Test;

public class NotationTest {
  @Test
  public void testArePropertiesDeclared() {
    Notation notation = new Notation();
    assertTrue(notation.arePropertiesDeclared());
  }

  @Test
  public void testType() {
    Notation notation = new Notation();
    assertEquals("notation", notation.getType());
  }

  @Test
  public void testLayers() {
    Notation notation = new Notation();
    notation.layer("a", "b");
    assertEquals("notation", notation.getType());
    assertEquals(Lists.newArrayList("a", "b"), notation.getLayers());
  }

  @Test
  public void testCopyNotation() {
    Notation notation = (Notation) new Notation()
        .format("test-format").notation("ABC")
        .name("test-name")
        .layer("test-layer");
    Notation notationCopy = notation.copy();
    assertEquals("test-name", notationCopy.getName());
    assertEquals("test-format", notationCopy.getFormat());
    assertEquals("ABC", notationCopy.getNotation());
    assertEquals("test-layer", notationCopy.getLayers().iterator().next());
  }
}