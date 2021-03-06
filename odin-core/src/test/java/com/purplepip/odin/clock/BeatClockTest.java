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

package com.purplepip.odin.clock;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.math.Rationals;
import com.purplepip.odin.math.Wholes;
import org.junit.jupiter.api.Test;

public class BeatClockTest {
  @Test
  public void testBeatClock() {
    BeatClock beatClock = newPrecisionBeatClock(60);
    assertEquals("Beats per minute not correct",
        60, beatClock.getBeatsPerMinute().getBeatsPerMinute());
    assertEquals("Tick unit not correct",
        Ticks.BEAT, beatClock.getTick());
    assertEquals("Count not correct",
        Wholes.ONE, beatClock.getPosition(1_000_000));
    assertEquals("Count not correct",
        Rationals.HALF, beatClock.getPosition(500_000));
  }

  @Test
  public void testMaxLookForward() {
    BeatClock beatClock = new PrecisionBeatClock(new BeatClock.Configuration()
        .staticBeatsPerMinute(60)
        .maxLookForwardInMillis(60_000));
    assertEquals(60, beatClock.getMaxLookForward().floor());
    assertEquals(60, beatClock.getMaxLookForward(Wholes.ONE).floor());
  }

  @Test
  public void testMovingClock() {
    MovableMicrosecondPositionProvider microsecondPositionProvider =
        new MovableMicrosecondPositionProvider();
    BeatClock beatClock = new PrecisionBeatClock(new BeatClock.Configuration()
        .staticBeatsPerMinute(60)
        .microsecondPositionProvider(microsecondPositionProvider)
        .startOffset(0));
    assertEquals(0, beatClock.getMicroseconds());
    assertEquals(60_000_000, beatClock.getMicroseconds(Wholes.valueOf(60)));
    /*
     * Setting the beat clock to 60 seconds, which is equivalent to moving 60 seconds into the
     * sequence (without time changing), should lead to beat 60 being now ...
     */
    beatClock.setMicroseconds(60_000_000);
    assertEquals(60_000_000, beatClock.getMicroseconds(Wholes.valueOf(60)));
    /*
     * Current beat is 60
     */
    assertEquals(Wholes.valueOf(60), beatClock.getPosition());


    /*
     * Beat 60 seconds in the future will be ...
     */
    assertEquals(Wholes.valueOf(60), beatClock.getPosition(60_000_000));

    /*
     * Current microsecond is ...
     */
    assertEquals(60_000_000, beatClock.getMicroseconds());

    /*
     * If instead of fast forwarding the performance moving to 10 seconds from now,
     * which is equivalent to waiting for 10 seconds.
     */
    beatClock.setMicroseconds(0);
    microsecondPositionProvider.incrementMicroseconds(10_000_000);
    assertEquals(10_000_000, beatClock.getMicroseconds());
    assertEquals(10_000_000, beatClock.getMicroseconds(Wholes.valueOf(10)));
    assertEquals(Wholes.valueOf(10), beatClock.getPosition());
    assertEquals(Wholes.valueOf(10), beatClock.getPosition(10_000_000));

    microsecondPositionProvider.setMicroseconds(0);
    /*
     * If fast forward the sequence to 60 seconds amd we wait 10 seconds ...
     */
    beatClock.setMicroseconds(60_000_000);
    microsecondPositionProvider.incrementMicroseconds(10_000_000);
    assertEquals(70_000_000, beatClock.getMicroseconds());
    assertEquals(50_000_000, beatClock.getMicroseconds(Wholes.valueOf(50)));
    assertEquals(Wholes.valueOf(70), beatClock.getPosition());
    assertEquals(Wholes.valueOf(20), beatClock.getPosition(20_000_000));
  }
}