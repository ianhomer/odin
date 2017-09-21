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

package com.purplepip.odin.sequence;

import static com.purplepip.odin.sequence.tick.Ticks.QUARTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import org.junit.Test;

public class TickConvertedClockTest {
  @Test
  public void testCount() {
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60));
    Clock clock = new TickConvertedClock(beatClock, () -> QUARTER, () -> 0L);
    assertTrue(clock.getPosition().gt(Wholes.MINUS_ONE));
    assertEquals(QUARTER, clock.getTick());
  }

  @Test
  public void testDuration() {
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60));
    Clock clock = new TickConvertedClock(beatClock, () -> QUARTER, () -> 0L);
    assertEquals(Whole.valueOf(4), clock.getDuration(1000000));
    assertEquals(Whole.valueOf(4), clock.getDuration(1000000, Whole.valueOf(10)));
  }

  @Test
  public void testMovingClock() {
    MovableMicrosecondPositionProvider microsecondPositionProvider =
        new MovableMicrosecondPositionProvider();
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60), microsecondPositionProvider);
    Clock clock = new TickConvertedClock(beatClock, () -> QUARTER, () -> 0L);
    assertEquals(Wholes.ZERO, beatClock.getPosition());
    assertEquals(Wholes.ZERO, clock.getPosition());
    assertEquals(Whole.valueOf(10), beatClock.getPosition(10_000_000));
    assertEquals(Whole.valueOf(40), clock.getPosition(10_000_000));
    /*
     * Move 60 seconds into the future
     */
    microsecondPositionProvider.setMicroseconds(60_000_000);
    assertEquals(Whole.valueOf(70), beatClock.getPosition(70_000_000));
    assertEquals(Whole.valueOf(280), clock.getPosition(70_000_000));
    assertEquals(Whole.valueOf(60), beatClock.getPosition());
    assertEquals(Whole.valueOf(240), clock.getPosition());
    /*
     * Fast forward sequence to 30 seconds in
     */
    beatClock.setMicroseconds(30_000_000);
    assertEquals(Whole.valueOf(40), beatClock.getPosition(70_000_000));
    assertEquals(Whole.valueOf(160), clock.getPosition(70_000_000));
    assertEquals(60_000_000, beatClock.getMicroseconds());
    assertEquals(Whole.valueOf(30), beatClock.getPosition());
    assertEquals(Whole.valueOf(120), clock.getPosition());
  }
}