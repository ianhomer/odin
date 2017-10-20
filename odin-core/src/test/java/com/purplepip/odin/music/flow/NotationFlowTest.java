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

package com.purplepip.odin.music.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.EventsListStringifier;
import com.purplepip.odin.math.Rationals;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.flow.DefaultFlowConfiguration;
import com.purplepip.odin.sequence.flow.Flow;
import com.purplepip.odin.sequence.flow.FlowFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.sequence.tick.MovableTock;
import com.purplepip.odin.sequence.tick.Ticks;
import com.purplepip.odin.sequencer.ProjectBuilder;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class NotationFlowTest {
  private Flow<Sequence, Note> createNotationFlow(String notationAsString) {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addNotation(Ticks.BEAT, notationAsString);
    Notation notation = (Notation) builder.getSequenceByOrder(0);
    FlowFactory<Note> flowFactory = new FlowFactory<>(new DefaultFlowConfiguration());
    BeatClock clock = new BeatClock(new StaticBeatsPerMinute(60));
    MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
    return flowFactory.createFlow(notation, clock, measureProvider);
  }

  @Test
  public void testGetNextEvent() {
    Flow<Sequence, Note> flow = createNotationFlow("B5/q, E5, G5, C5");
    Event<Note> event = flow
        .getNextEvent(new MovableTock(Ticks.BEAT, Rationals.MINUS_ONE));
    assertEquals(Wholes.ZERO, event.getTime());
    assertEquals(83, event.getValue().getNumber());
  }

  @Test
  public void testGetMultipleEvents() {
    Flow<Sequence, Note> flow = createNotationFlow("B5/8, B5, E5/q, G5, C5");
    List<Event> events = new ArrayList<>();
    Real previousEventTime = Rationals.MINUS_ONE;
    for (int i = 0; i < 10 ;i++) {
      Event event = flow.getNextEvent(new MovableTock(Ticks.BEAT, previousEventTime));
      assertTrue("Event should be after previous one ; " + event.getTime()
              + " is not greater than " + previousEventTime,
          event.getTime().gt(previousEventTime));
      events.add(event);
      previousEventTime = event.getTime();
    }

    assertEquals("0=83--½;½=83--½;1=76--;2=79--;3=72--;4=83--½;4½=83--½;5=76--;6=79--;7=72--;",
        new EventsListStringifier(events).toString());
  }
}