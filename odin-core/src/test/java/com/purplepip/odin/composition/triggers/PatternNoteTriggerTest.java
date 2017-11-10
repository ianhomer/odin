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

package com.purplepip.odin.composition.triggers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequencer.Operation;
import org.junit.Test;

public class PatternNoteTriggerTest {
  @Test
  public void testIsTriggeredBy() {
    Pattern pattern = new Pattern();
    pattern.setName("test-pattern");
    pattern.setNote(new DefaultNote(62, 50,1));
    PatternNoteTrigger trigger = new PatternNoteTrigger();
    trigger.setPatternName("test-pattern");
    trigger.inject(pattern);
    Operation operation = new NoteOnOperation(1, 62, 50);
    assertTrue(trigger.isTriggeredBy(operation));
    assertFalse(trigger.isTriggeredBy(new NoteOnOperation(1, 61, 50)));
  }
}