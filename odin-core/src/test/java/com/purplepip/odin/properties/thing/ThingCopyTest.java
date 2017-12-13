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

import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.sequence.Notation;
import org.junit.Test;

public class ThingCopyTest {
  @Test
  public void testCopyFromGeneric() {
    GenericSequence destination = new Notation().name("test");
    GenericSequence source = new GenericSequence().property("offset", "8");
    new ThingCopy().from(source).to(destination).copy();
    assertEquals("8", destination.getProperty("offset"));
    assertEquals(Whole.valueOf(8), destination.getOffset());
  }

  @Test
  public void testCopyFromPlugin() {
    GenericSequence destination = new Notation().name("test").tick(Ticks.BEAT);
    GenericSequence source = new Notation().tick(Ticks.HALF);
    new ThingCopy().from(source).to(destination).copy();
    assertEquals(Ticks.HALF, destination.getTick());
  }

}