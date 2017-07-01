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
import com.purplepip.odin.sequence.tick.RuntimeTicks;
import com.purplepip.odin.sequence.tick.Tick;

/**
 * Clock for the given sequence runtime.
 */
public class SequenceRuntimeClock implements Clock {
  private Tick tick;
  private BeatClock beatClock;
  private TickConverter beatToTickConverter;

  /**
   * Create new sequence runtime clock.
   *
   * @param beatClock clock
   * @param tick tick for the sequence runtime
   * @param offsetProvider offset provider for the sequence runtime
   */
  public SequenceRuntimeClock(BeatClock beatClock, Tick tick, OffsetProvider offsetProvider) {
    this.tick = tick;
    this.beatClock = beatClock;
    beatToTickConverter = new DefaultTickConverter(beatClock,
        RuntimeTicks.BEAT, new ImmutableRuntimeTick(tick), offsetProvider);
  }
  
  @Override
  public Tick getTick() {
    return tick;
  }

  @Override
  public long getMicroseconds() {
    return beatClock.getMicroseconds();
  }

  /*
   * TODO : Convert count to beat before getting ms on beat clock.
   */
  @Override
  public long getMicroseconds(long count) {
    return beatClock.getMicroseconds(count);
  }

  /*
   * TODO : Convert count to beat before getting ms on beat clock.
   */
  @Override
  public long getMicroseconds(double count) {
    return beatClock.getMicroseconds(count);
  }

  @Override
  public long getCount() {
    return beatToTickConverter.convert(beatClock.getCount());
  }

  @Override
  public long getCount(long microseconds) {
    return 0;
  }
}
