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

package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.SameTimeUnitTickConverter;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.measure.ConvertedMeasureProvider;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.sequence.tick.Ticks;
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

    assertEquals(0, measureProvider.getCount(0), 0.01);
    assertEquals(1, measureProvider.getCount(1), 0.01);
    assertEquals(7, measureProvider.getCount(7), 0.01);
    assertEquals(4, measureProvider.getCount(12), 0.01);
  }


  @Test
  public void testFourThirdsBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.FOUR_THIRDS));

    assertEquals(0, measureProvider.getCount(9), 0.001);
    assertEquals(1, measureProvider.getCount(10), 0.01);
  }

  @Test
  public void testQuarterBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.QUARTER));

    assertEquals(16, measureProvider.getTicksInMeasure(0), 0.001);
  }

  @Test
  public void testThreeQuarterBeats() {
    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        new SameTimeUnitTickConverter(() -> Ticks.BEAT, () -> Ticks.THREE_QUARTERS));
    assertEquals(5.333, measureProvider.getTicksInMeasure(0), 0.001);
  }

  @Test
  public void testMicroseconds() {
    BeatClock beatClock = new BeatClock(new StaticBeatsPerMinute(60));
    DefaultTickConverter tickConverter = new DefaultTickConverter(beatClock,
        () -> Ticks.BEAT, () -> Ticks.MICROSECOND, () -> 0L);

    MeasureProvider measureProvider = new ConvertedMeasureProvider(staticMeasureProvider,
        tickConverter);
    assertEquals(1000, measureProvider.getCount(1000), 0.001);
    assertEquals(2000, measureProvider.getCount(4002000), 0.001);
  }
}
