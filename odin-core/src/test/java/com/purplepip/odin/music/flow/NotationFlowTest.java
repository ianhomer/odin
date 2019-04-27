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

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.clock.tick.MovableTock;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.flow.Flow;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.EventsListStringifier;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class NotationFlowTest {
  private Flow<Sequence> createNotationFlow(String notationAsString) {
    TransientPerformance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new DefaultPerformanceContainer(project));
    builder.withName("notation").addNotation(Ticks.BEAT, notationAsString);
    Notation notation = (Notation) builder.getSequenceByOrder(0);
    FlowFactory flowFactory = newNoteFlowFactory();
    BeatClock clock = newPrecisionBeatClock(60);
    MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
    return flowFactory.createFlow(notation, clock, measureProvider);
  }

  @Test
  void testGetNextEvent() {
    Flow<Sequence> flow = createNotationFlow("B5/q, E5, G5, C5");
    flow.initialise();
    Event event = flow.getNextEvent(new MovableTock(Ticks.BEAT, Wholes.MINUS_ONE));
    LOG.debug("Clock : {}", flow.getContext().getClock());
    assertEquals(Wholes.ZERO, event.getTime());
    assertTrue(event.getValue() instanceof Note);
    assertEquals(83, ((Note) event.getValue()).getNumber());
  }

  @Test
  void testGetMultipleEvents() {
    Flow<Sequence> flow = createNotationFlow("B5/8, B5, E5/q, G5, C5");
    flow.initialise();
    List<Event> events = new ArrayList<>();
    Real previousEventTime = Wholes.MINUS_ONE;
    for (int i = 0; i < 10; i++) {
      Event event = flow.getNextEvent(new MovableTock(Ticks.BEAT, previousEventTime));
      assertTrue(
          "Event should be after previous one ; "
              + event.getTime()
              + " is not greater than "
              + previousEventTime,
          event.getTime().gt(previousEventTime));
      events.add(event);
      previousEventTime = event.getTime();
    }

    assertEquals(
        "0=83--½;½=83--½;1=76--;2=79--;3=72--;4=83--½;4½=83--½;5=76--;6=79--;7=72--;",
        new EventsListStringifier(events).toString());
  }
}
