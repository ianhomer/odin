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

import static com.purplepip.odin.math.LessThan.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import org.junit.jupiter.api.Test;

class IndexedCompositionTest {
  @Test
  void testGetEvent() {
    EventsCompositionBuilder builder = new EventsCompositionBuilder();
    builder.addNote(new DefaultNote(99,50,1));
    builder.addNote(new DefaultNote(98,50,1));
    IndexedComposition indexedComposition = new IndexedComposition(builder.create());
    assertEquals(99, ((Note) indexedComposition
        .getEventAfter(lessThan(Wholes.ZERO)).getValue()).getNumber());
    assertEquals(98, ((Note) indexedComposition
        .getEventAfter(lessThan(Wholes.ONE)).getValue()).getNumber());
    assertNull(indexedComposition.getEventAfter(lessThan(Wholes.TWO)));
  }
}