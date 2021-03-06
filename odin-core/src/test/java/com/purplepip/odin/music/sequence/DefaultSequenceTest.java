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

import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import org.junit.jupiter.api.Test;

class DefaultSequenceTest {
  @Test
  void testCreateSequence() {
    assertSequenceOk(createSequence());
  }

  @Test
  void testCopySequence() {
    assertSequenceOk(createSequence().copy());
  }

  private void assertSequenceOk(SequenceConfiguration sequence) {
    assertEquals("test-sequence", sequence.getName());
    assertEquals("my.property-value", sequence.getProperty("my.property"));
    assertEquals("test-flow-name", sequence.getType());
  }

  private SequenceConfiguration createSequence() {
    GenericSequence sequence = new GenericSequence("test-flow-name");
    sequence.setName("test-sequence");
    sequence.setProperty("my.property","my.property-value");
    return sequence;
  }
}