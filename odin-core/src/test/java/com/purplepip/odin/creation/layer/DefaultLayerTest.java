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

package com.purplepip.odin.creation.layer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultLayerTest {
  @Test
  public void testCreate() {
    DefaultLayer layer = new DefaultLayer().name("test");
    assertEquals("test", layer.getName());
  }

  @Test
  public void testCopy() {
    DefaultLayer layer = new DefaultLayer().name("test");
    layer.layer("layer-1", "layer-2");
    DefaultLayer copy = layer.copy();
    assertEquals("test", copy.getName());
    assertEquals(2, copy.getLayers().size());
    assertEquals("layer-1", copy.getLayers().get(0));
    assertEquals("layer-2", copy.getLayers().get(1));
  }
}