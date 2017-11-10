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

import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.properties.runtime.Property;

/**
 * Clock for the given roll.
 */
public class TickConvertedClock extends AbstractClock {
  private Property<Tick> tick;
  private BeatClock beatClock;
  private TickConverter tickToBeatConverter;

  /**
   * Create new sequence runtime clock.
   *
   * @param beatClock clock
   * @param tick tick for the sequence runtime
   * @param offset offset property for the sequence roll
   */
  public TickConvertedClock(BeatClock beatClock, Property<Tick> tick,
                            Property<Long> offset) {
    this.tick = tick;
    this.beatClock = beatClock;
    tickToBeatConverter = new DefaultTickConverter(beatClock,
        tick, () -> Ticks.BEAT, offset);
  }

  @Override
  public Tick getTick() {
    return tick.get();
  }

  @Override
  public long getMicroseconds() {
    return beatClock.getMicroseconds();
  }

  @Override
  public long getMicroseconds(Real count) {
    return beatClock.getMicroseconds(tickToBeatConverter.convert(count));
  }

  @Override
  public Real getPosition() {
    return tickToBeatConverter.convertBack(beatClock.getPosition());
  }

  @Override
  public Real getPosition(long microseconds) {
    return tickToBeatConverter.convertBack(beatClock.getPosition(microseconds));
  }
}
