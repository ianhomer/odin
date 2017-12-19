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

package com.purplepip.odin.properties.beany;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.math.Wholes;
import org.junit.Test;

public class ResetterTest {
  @Test
  public void testReset() {
    GenericSequence sequence = new GenericSequence();
    Resetter setter = new Resetter();
    setter.reset(sequence);
    setter.set("format", "test-format");
    setter.set("offset", 9);
    assertEquals("test-format", sequence.getProperty("format"));
    assertEquals(Wholes.valueOf(9), sequence.getOffset());
    GenericSequence newSequence = new GenericSequence();
    setter.reset(newSequence);
    assertEquals("test-format", newSequence.getProperty("format"));
    assertEquals(Wholes.valueOf(9), newSequence.getOffset());
  }
}
