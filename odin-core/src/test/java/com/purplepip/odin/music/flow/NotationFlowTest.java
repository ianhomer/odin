/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

import com.purplepip.odin.events.Event;
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
import org.junit.Test;

public class NotationFlowTest {
  @Test
  public void testGetNextEvent() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addNotation(Ticks.BEAT, "a");
    Notation notation = (Notation) builder.getSequenceByOrder(0);
    FlowFactory<Note> flowFactory = new FlowFactory<>(new DefaultFlowConfiguration());
    BeatClock clock = new BeatClock(new StaticBeatsPerMinute(60));
    MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
    Flow<Sequence, Note> flow = flowFactory.createFlow(notation, clock, measureProvider);
    Event<Note> event = flow
        .getNextEvent(new MovableTock(notation.getTick(), 0));
    assertEquals(1, event.getTime());
    assertEquals(60, event.getValue().getNumber());
  }
}