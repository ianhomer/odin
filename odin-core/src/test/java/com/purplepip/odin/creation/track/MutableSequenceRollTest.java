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

package com.purplepip.odin.creation.track;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.clock.measure.StaticBeatMeasureProvider.newMeasureProvider;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.MovableMicrosecondPositionProvider;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MutableSequenceRollTest {
  private FlowFactory flowFactory = newNoteFlowFactory();
  private MovableMicrosecondPositionProvider microsecondPositionProvider =
      new MovableMicrosecondPositionProvider();
  private BeatClock clock = newPrecisionBeatClock(60, microsecondPositionProvider);
  private MeasureProvider measureProvider = newMeasureProvider(4);

  @Test
  public void testStart() {
    microsecondPositionProvider.setMicroseconds(0);
    clock.start();
    GenericSequence notation = new Notation()
        .notation("C D E")
        .channel(2).layer("groove")
        .enabled(false)
        .name("success");
    MutableSequenceRoll roll = new MutableSequenceRoll(notation.copy(), clock,
        flowFactory, measureProvider);
    Event nextEvent = roll.pop();
    assertNull("Next event of disabled sequence should be null", nextEvent);
    microsecondPositionProvider.setMicroseconds(4000000);
    /*
     * Start roll and then verify that events come out from the start position.
     */
    roll.start();
    assertEquals(Wholes.valueOf(8), roll.getSequence().getOffset());
    nextEvent = roll.pop();
    LOG.debug("Next Event : {}", nextEvent);
    assertNotNull("Next event after roll started should not be null", nextEvent);
    assertTrue(nextEvent.getValue() instanceof Note);
    assertEquals(60,((Note) nextEvent.getValue()).getNumber());
    assertEquals(Wholes.ZERO, nextEvent.getTime());


    /*
     * Test tick change
     */
    LOG.debug("Changing tick to {}", Ticks.HALF);
    notation.setTick(Ticks.HALF);
    roll.setSequence(notation);
    assertEquals(Ticks.HALF, roll.getTick().get());
    nextEvent = roll.pop();
    LOG.debug("Next Event : {}", nextEvent);
    assertEquals(60, ((Note) nextEvent.getValue()).getNumber());
    assertEquals(Wholes.valueOf(8), nextEvent.getTime());

    /*
     * Test change offset
     */
    notation.offset(20);
    roll.setSequence(notation.copy());
    assertEquals(Wholes.valueOf(20), roll.getOffsetProperty().get());
    nextEvent = roll.pop();
    LOG.debug("Next Event : {}", nextEvent);
    assertEquals(62, ((Note) nextEvent.getValue()).getNumber());
    assertEquals(Wholes.valueOf(9), nextEvent.getTime());
  }
}