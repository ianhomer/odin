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

package com.purplepip.odin.creation.sequence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GenericSequenceTest {
  @Test
  public void testCreateGenericSequence() {
    SequenceConfiguration sequence = new GenericSequence("test");
    assertEquals("test", sequence.getType());
  }

  @Test
  public void testDefaultEndless() {
    SequenceConfiguration sequence = new GenericSequence("test");
    assertTrue(sequence.isEndless());
  }

  @Test
  public void testEndless() {
    SequenceConfiguration sequence = new GenericSequence("test").length(-1);
    assertTrue(sequence.isEndless());
  }

  @Test
  public void testNotEndless() {
    SequenceConfiguration sequence = new GenericSequence("test").length(1);
    assertFalse(sequence.isEndless());
  }

  @Test
  public void testArePropertiesDeclared() {
    SequenceConfiguration sequence = new GenericSequence("test");
    assertFalse("Generic sequence should not have properties declared",
        sequence.arePropertiesDeclared());
  }
}