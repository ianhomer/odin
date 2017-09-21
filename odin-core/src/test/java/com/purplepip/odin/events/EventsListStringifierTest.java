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

package com.purplepip.odin.events;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class EventsListStringifierTest {
  @Test
  public void testToString() {
    List<Event> events = new ArrayList<>();
    events.add(new DefaultEvent<Note>(new DefaultNote(60,40,1), 0));
    events.add(new DefaultEvent<Note>(new DefaultNote(60,40,2), 0));
    events.add(new DefaultEvent<Note>(new DefaultNote(49,50,1), 1));
    events.add(new DefaultEvent<Note>(new DefaultNote(49,60,3), 1));
    assertEquals("0=60--;0=60--2;1=49-50-;1=49-60-3;",
        new EventsListStringifier(events).toString());
  }
}
