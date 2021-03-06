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

package com.purplepip.odin.music.composition.events;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import org.junit.jupiter.api.Test;

class CompositionRollTest {
  @Test
  void testPop() {
    EventsCompositionBuilder builder = new EventsCompositionBuilder();
    builder.addNote(new DefaultNote(99,50,40));
    CompositionRoll roll = new CompositionRoll(builder.create());
    assertEquals(99, ((Note) roll.pop().getValue()).getNumber());
  }
}