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

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.sequence.tick.Ticks;
import org.junit.Test;

public class BeatClockTest {
  @Test
  public void testBeatClock() {
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60));
    assertEquals("Beats per minute not correct",
        60, beatClock.getBeatsPerMinute().getBeatsPerMinute());
    assertEquals("Tick unit not correct",
        Ticks.BEAT, beatClock.getTick());
    assertEquals("Count not correct",
        Wholes.ONE, beatClock.getCount(1000000));
  }
}