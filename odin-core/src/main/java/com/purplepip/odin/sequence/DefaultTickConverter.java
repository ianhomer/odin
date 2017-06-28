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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequence.tick.RuntimeTick;

/**
 * Time unit converter.
 */
public class DefaultTickConverter extends AbstractTickConverter {
  private Clock clock;

  /**
   * Create a tick converter.
   *
   * @param clock clock
   * @param inputTick input tick
   * @param outputTick output tick
   * @param inputOffsetProvider input offset provider
   */
  public DefaultTickConverter(Clock clock, RuntimeTick inputTick, RuntimeTick outputTick,
                              OffsetProvider inputOffsetProvider) {
    if (clock == null) {
      throw new OdinRuntimeException("Clock must not be null");
    }
    this.clock = clock;
    setInputTick(inputTick);
    setOutputTick(outputTick);
    setInputOffsetProvider(inputOffsetProvider);
  }

  @Override
  protected long getTimeUnitAsBeat(long time) {
    switch (getInputTick().getTimeUnit()) {
      case BEAT:
        return scaleTime(time);
      case MICROSECOND:
        return (long) (clock.getBeat(getInputTick().getFactorAsInt() * time)
            / getOutputTick().getFactor());
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  @Override
  protected long getTimeUnitAsMicrosecond(long time) {
    switch (getInputTick().getTimeUnit()) {
      case BEAT:
        return clock.getMicroSeconds(getInputTick().getFactor() * time)
            / getOutputTick().getFactorAsInt();
      case MICROSECOND:
        return scaleTime(time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }
}
