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
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import org.junit.Before;
import org.junit.Test;

/**
 * Converted measure provider test.
 */

public class ConvertedMeasureProviderTest {
  private MeasureProvider staticMeasureProvider;

  @Before
  public void setUp() {
    staticMeasureProvider = new StaticBeatMeasureProvider(4);
  }

  @Test
  public void testHalfBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.HALF));

    assertEquals(Wholes.ZERO, measureProvider.getCount(Wholes.ZERO));
    assertEquals(Wholes.ONE, measureProvider.getCount(Wholes.ONE));
    assertEquals(Whole.valueOf(7), measureProvider.getCount(Whole.valueOf(7)));
    assertEquals(Whole.valueOf(4), measureProvider.getCount(Whole.valueOf(12)));
    assertEquals(Whole.valueOf(16), measureProvider.getNextMeasureStart(Whole.valueOf(11)));
  }


  @Test
  public void testFourThirdsBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.FOUR_THIRDS));

    assertEquals(Wholes.ZERO, measureProvider.getCount(Whole.valueOf(9)));
    assertEquals(Wholes.ONE, measureProvider.getCount(Whole.valueOf(10)));
  }

  @Test
  public void testQuarterBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.QUARTER));

    assertEquals(Whole.valueOf(16), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }

  @Test
  public void testThreeQuarterBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.THREE_QUARTERS));
    assertEquals(Whole.valueOf(16, 3), measureProvider.getTicksInMeasure(Wholes.ZERO));
  }

  @Test
  public void testMicroseconds() {
    BeatClock beatClock = newPrecisionBeatClock(60);
    DefaultTickConverter tickConverter = new DefaultTickConverter(beatClock,
        () -> Ticks.BEAT, () -> Ticks.MICROSECOND, () -> 0L);

    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        tickConverter);
    assertEquals(Whole.valueOf(1000), measureProvider.getCount(Whole.valueOf(1000)));
    assertEquals(Whole.valueOf(2000), measureProvider.getCount(Whole.valueOf(4002000)));
  }
}
