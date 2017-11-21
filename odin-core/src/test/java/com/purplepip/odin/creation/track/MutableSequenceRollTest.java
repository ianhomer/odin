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

import static com.purplepip.odin.clock.BeatClock.newBeatClock;
import static com.purplepip.odin.clock.measure.StaticBeatMeasureProvider.newMeasureProvider;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.MovableMicrosecondPositionProvider;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MutableSequenceRollTest {
  private FlowFactory<Note> flowFactory = newNoteFlowFactory();
  private MovableMicrosecondPositionProvider microsecondPositionProvider =
      new MovableMicrosecondPositionProvider();
  private BeatClock clock = newBeatClock(60, microsecondPositionProvider);
  private MeasureProvider measureProvider = newMeasureProvider(4);

  @Test
  public void testStart() {
    microsecondPositionProvider.setMicroseconds(0);
    clock.start();
    MutableSequenceRoll<Note> roll = new MutableSequenceRoll<>(clock, flowFactory, measureProvider);
    GenericSequence notation = new Notation()
        .notation("C D E")
        .channel(2).layer("groove")
        .enabled(false)
        .name("success");
    roll.setSequence(notation);
    Event<Note> nextEvent = roll.pop();
    assertNull("Next event of disabled sequence should be null", nextEvent);
    microsecondPositionProvider.setMicroseconds(4000000);
    /*
     * Start roll and then verify that events come out from the start position.
     */
    roll.start();
    assertEquals(4, roll.getSequence().getOffset());
    nextEvent = roll.pop();
    LOG.info("Next Event : {}", nextEvent);
    assertNotNull("Next event after roll started should not be null", nextEvent);
  }
}