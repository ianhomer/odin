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

import com.purplepip.odin.math.Whole;
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
        Wholes.ONE, beatClock.getPosition(1_000_000));
  }

  @Test
  public void testMovingClock() {
    MovableMicrosecondPositionProvider microsecondPositionProvider =
        new MovableMicrosecondPositionProvider();
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60), microsecondPositionProvider);
    assertEquals(0, beatClock.getMicroseconds());
    assertEquals(60_000_000, beatClock.getMicroseconds(Whole.valueOf(60)));
    /*
     * Setting the beat clock to 60 seconds, which is equivalent to moving 60 seconds into the
     * sequence (without time changing), should lead to beat 60 being now ...
     */
    beatClock.setMicroseconds(60_000_000);
    assertEquals(0, beatClock.getMicroseconds(Whole.valueOf(60)));
    /*
     * Current beat is 60
     */
    assertEquals(Whole.valueOf(60), beatClock.getPosition());
    /*
     * Beat 60 seconds in the future will be 120
     */
    assertEquals(Whole.valueOf(120), beatClock.getPosition(60_000_000));

    /*
     * Current microsecond is still 0
     */
    assertEquals(0, beatClock.getMicroseconds());

    /*
     * If instead of fast forwarding the music we move microseconds to 10 seconds from now,
     * which is equivalent to waiting for 10 seconds.
     */
    beatClock.setMicroseconds(0);
    microsecondPositionProvider.setMicroseconds(10_000_000);
    assertEquals(10_000_000, beatClock.getMicroseconds());
    assertEquals(10_000_000, beatClock.getMicroseconds(Whole.valueOf(10)));
    assertEquals(Whole.valueOf(10), beatClock.getPosition());
    assertEquals(Whole.valueOf(10), beatClock.getPosition(10_000_000));

    /*
     * If we wait 10 second and fast forward the sequence to 60 seconds in ...
     */
    microsecondPositionProvider.setMicroseconds(10_000_000);
    beatClock.setMicroseconds(60_000_000);
    assertEquals(10_000_000, beatClock.getMicroseconds());
    assertEquals(0, beatClock.getMicroseconds(Whole.valueOf(50)));
    assertEquals(Whole.valueOf(60), beatClock.getPosition());
    assertEquals(Whole.valueOf(70), beatClock.getPosition(20_000_000));
  }
}