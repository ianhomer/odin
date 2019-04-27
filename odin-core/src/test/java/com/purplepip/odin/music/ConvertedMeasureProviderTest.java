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

package com.purplepip.odin.music;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.ConvertedMeasureProvider;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.SameTimeUnitTickConverter;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.math.Rationals;
import com.purplepip.odin.math.Wholes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Converted measure provider test.
 */

class ConvertedMeasureProviderTest {
  private MeasureProvider staticMeasureProvider;

  @BeforeEach
  void setUp() {
    staticMeasureProvider = new StaticBeatMeasureProvider(4);
  }

  @Test
  void testHalfBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.HALF));

    assertEquals(Wholes.ZERO, measureProvider.getCount(Wholes.ZERO));
    assertEquals(Wholes.ONE, measureProvider.getCount(Wholes.ONE));
    assertEquals(Wholes.valueOf(7), measureProvider.getCount(Wholes.valueOf(7)));
    assertEquals(Wholes.valueOf(4), measureProvider.getCount(Wholes.valueOf(12)));
    assertEquals(Wholes.valueOf(16), measureProvider.getNextMeasureStart(Wholes.valueOf(11)));
  }


  @Test
  void testFourThirdsBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.FOUR_THIRDS));

    assertEquals(Wholes.ZERO, measureProvider.getCount(Wholes.valueOf(9)));
    assertEquals(Wholes.ONE, measureProvider.getCount(Wholes.valueOf(10)));
  }

  @Test
  void testQuarterBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.QUARTER));

    assertEquals(Wholes.valueOf(16), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }

  @Test
  void testThreeQuarterBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.THREE_QUARTERS));
    assertEquals(Rationals.valueOf(16, 3), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }

  @Test
  void testMicroseconds() {
    BeatClock beatClock = newPrecisionBeatClock(60);
    DefaultTickConverter tickConverter = new DefaultTickConverter(beatClock,
        () -> Ticks.BEAT, () -> Ticks.MICROSECOND, () -> Wholes.ZERO);

    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        tickConverter);
    assertEquals(Wholes.valueOf(1000), measureProvider.getCount(Wholes.valueOf(1000)));
    assertEquals(Wholes.valueOf(2000), measureProvider.getCount(Wholes.valueOf(4002000)));
  }
}
