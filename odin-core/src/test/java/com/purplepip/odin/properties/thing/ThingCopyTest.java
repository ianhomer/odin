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

package com.purplepip.odin.properties.thing;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.music.sequence.Notation;
import org.junit.Test;

public class ThingCopyTest {
  @Test
  public void testCopy() {
    GenericSequence notation = new Notation().name("test");
    GenericSequence sequence = new GenericSequence().property("offset", "8");
    new ThingCopy().from(sequence).to(notation).copy();
    assertEquals("8", notation.getProperty("offset"));
    assertEquals(8, notation.getOffset());
  }
}