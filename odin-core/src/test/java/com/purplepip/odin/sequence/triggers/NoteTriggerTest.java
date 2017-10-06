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

package com.purplepip.odin.sequence.triggers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.music.operations.NoteOnOperation;
import org.junit.Test;

public class NoteTriggerTest {
  @Test
  public void testGetMessage() {
    NoteTrigger messageTrigger = new NoteTrigger();
    messageTrigger.setNote(60);
    assertEquals(60, messageTrigger.getNote());
  }

  @Test
  public void testMatches() throws Exception {
    NoteTrigger trigger = new NoteTrigger();
    trigger.setNote(60);
    assertFalse(trigger.isTriggeredBy(new NoteOnOperation(0,61,50)));
    assertTrue(trigger.isTriggeredBy(new NoteOnOperation(0,60,50)));
  }
}