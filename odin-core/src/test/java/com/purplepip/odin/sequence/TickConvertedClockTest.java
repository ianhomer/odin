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

package com.purplepip.odin.sequence;

import static com.purplepip.odin.sequence.tick.Ticks.QUARTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import org.junit.Test;

public class TickConvertedClockTest {
  @Test
  public void testCount() {
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60));
    Clock clock = new TickConvertedClock(beatClock, () -> QUARTER, () -> 0L);
    assertTrue(clock.getCount().gt(Wholes.MINUS_ONE));
    assertEquals(QUARTER, clock.getTick());
  }

  @Test
  public void testDuration() {
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60));
    Clock clock = new TickConvertedClock(beatClock, () -> QUARTER, () -> 0L);
    assertEquals(Real.valueOf(4), clock.getDuration(1000000));
    assertEquals(Real.valueOf(4), clock.getDuration(1000000, Real.valueOf(10)));
  }
}