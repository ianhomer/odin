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

package com.purplepip.odin.creation.tick;

import static com.purplepip.odin.creation.tick.Ticks.HALF;
import static com.purplepip.odin.creation.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.MovableMicrosecondPositionProvider;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import org.junit.Before;
import org.junit.Test;

public class MovingClockTickConverterTest {
  private MovableMicrosecondPositionProvider microsecondPositionProvider;
  private BeatClock beatClock;
  private TickConverter tickConverter;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    microsecondPositionProvider = new MovableMicrosecondPositionProvider();
    beatClock = new BeatClock(new StaticBeatsPerMinute(60), microsecondPositionProvider);
    tickConverter = new DefaultTickConverter(beatClock, () -> MILLISECOND, () -> HALF, () -> 0L);
  }

  @Test
  public void testMovingClock() {

    assertEquals(Wholes.ZERO, tickConverter.convert(Wholes.ZERO));
    assertEquals(Whole.valueOf(120), tickConverter.convert(Whole.valueOf(60_000)));

    /*
     * If we wait ten seconds the conversions are still the same ...
     */
    microsecondPositionProvider.setMicroseconds(10_000_000);
    assertEquals(Wholes.ZERO, tickConverter.convert(Wholes.ZERO));
    assertEquals(Whole.valueOf(120), tickConverter.convert(Whole.valueOf(60_000)));
  }

  @Test
  public void testMovingBeatClock() {

    /*
     * If instead we move 60 seconds into the sequence ...
     */
    microsecondPositionProvider.setMicroseconds(0);
    beatClock.setMicroseconds(60_000_000);
    assertEquals(Whole.valueOf(120), tickConverter.convert(Wholes.ZERO));
    assertEquals(Whole.valueOf(140), tickConverter.convert(Whole.valueOf(10_000)));
    assertEquals(Whole.valueOf(240), tickConverter.convert(Whole.valueOf(60_000)));

    /*
     * If we wait 10 seconds and then move sequence to 60 seconds in ...
     */
    microsecondPositionProvider.setMicroseconds(10_000_000);
    beatClock.setMicroseconds(60_000_000);
    assertEquals(Whole.valueOf(100), tickConverter.convert(Wholes.ZERO));
    assertEquals(Whole.valueOf(120), tickConverter.convert(Whole.valueOf(10_000)));
    assertEquals(Whole.valueOf(220), tickConverter.convert(Whole.valueOf(60_000)));
  }
}
