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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.Loop;
import com.purplepip.odin.sequence.flow.StaticFlowContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class RandomTest {
  private static final int LOWER_LIMIT = 60;
  private static final int UPPER_LIMIT = 72;

  @Test
  public void testGetNextEvent() {
    Random sequence = new Random();
    sequence.setBits(1);
    sequence.setLowerLimit(LOWER_LIMIT);
    sequence.setUpperLimit(UPPER_LIMIT);
    sequence.afterPropertiesSet();
    FlowContext context = new StaticFlowContext(60, 4);
    Event<Note> nextEvent = sequence.getNextEvent(context, new Loop());
    assertNotNull("Event at first beat should not be null", nextEvent);
    assertEquals(Wholes.ZERO, nextEvent.getTime());
    int number = nextEvent.getValue().getNumber();
    LOG.debug("Random note number is {}", number);
    assertTrue(number >= LOWER_LIMIT);
    assertTrue(number <= UPPER_LIMIT);
    nextEvent = sequence.getNextEvent(context, new Loop(1));
    assertNull("Event at beat 2 should be null", nextEvent);
    assertEquals("Random note shouldn't change once initialised",
        number, sequence.getNextEvent(context, new Loop(3)).getValue().getNumber());
  }

  @Test
  public void testCopy() {
    Random sequence = new Random();
    sequence.setName("test-name");
    sequence.setBits(1);
    sequence.setLowerLimit(LOWER_LIMIT);
    sequence.setUpperLimit(UPPER_LIMIT);
    Random copy = sequence.copy();
    assertEquals(sequence, copy);
    assertEquals(sequence.getId(), copy.getId());
    assertEquals(sequence.getName(), copy.getName());
    assertEquals(sequence.getBits(), copy.getBits());
    assertEquals(sequence.getLowerLimit(), copy.getLowerLimit());
    assertEquals(sequence.getUpperLimit(), copy.getUpperLimit());
  }
}