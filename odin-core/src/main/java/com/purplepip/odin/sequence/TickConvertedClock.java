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

import com.purplepip.odin.sequence.tick.ImmutableRuntimeTick;
import com.purplepip.odin.sequence.tick.RuntimeTick;
import com.purplepip.odin.sequence.tick.RuntimeTicks;
import com.purplepip.odin.sequence.tick.Tick;

/**
 * Clock for the given roll.
 */
public class TickConvertedClock extends AbstractClock {
  private Tick tick;
  private BeatClock beatClock;
  private TickConverter tickToBeatConverter;

  /**
   * Create new sequence runtime clock.
   *
   * @param beatClock clock
   * @param tick tick for the sequence runtime
   * @param offsetProvider offset provider for the sequence runtime
   */
  public TickConvertedClock(BeatClock beatClock, Tick tick, OffsetProvider offsetProvider) {
    this.tick = tick;
    this.beatClock = beatClock;
    RuntimeTick runtimeTick = new ImmutableRuntimeTick(tick);
    // TODO : runtimeTick needs to be set via a provider that will update if track updates
    tickToBeatConverter = new DefaultTickConverter(beatClock,
        () -> runtimeTick, () -> RuntimeTicks.BEAT, offsetProvider);
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  @Override
  public long getMicroseconds() {
    return beatClock.getMicroseconds();
  }

  @Override
  public long getMicroseconds(long count) {
    return beatClock.getMicroseconds(tickToBeatConverter.convert(count));
  }

  @Override
  public long getMicroseconds(double count) {
    return beatClock.getMicroseconds(tickToBeatConverter.convert((long) count));
  }

  @Override
  public long getCount() {
    return tickToBeatConverter.convertBack(beatClock.getCount());
  }

  @Override
  public long getCount(long microseconds) {
    return tickToBeatConverter.convertBack(beatClock.getCount(microseconds));
  }

  /*
   * TODO : Count as double needs to retain double precision, currently convertBack always
   * rounds to long.
   */
  @Override
  public double getCountAsDouble(long microseconds) {
    return tickToBeatConverter.convertBack(beatClock.getCount(microseconds));
  }
}
