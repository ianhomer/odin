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
  private BeatClock clock;
  private TickConverter beatToTickConverter;

  /**
   * Create new sequence runtime clock.
   *
   * @param clock clock
   * @param tick tick for the sequence runtime
   * @param offsetProvider offset provider for the sequence runtime
   */
  public SequenceRuntimeClock(BeatClock clock, Tick tick, OffsetProvider offsetProvider) {
    this.tick = tick;
    this.clock = clock;
    beatToTickConverter = new DefaultTickConverter(clock,
        RuntimeTicks.BEAT, new ImmutableRuntimeTick(tick), offsetProvider);
  }

  @Override
  public long getMicrosecondPosition() {
    return clock.getMicrosecondPosition();
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  @Override
  // TODO : Should clock return longs not double?  Why do we need double?
  public double getCount() {
    return beatToTickConverter.convert((long) clock.getCount());
  }
}
