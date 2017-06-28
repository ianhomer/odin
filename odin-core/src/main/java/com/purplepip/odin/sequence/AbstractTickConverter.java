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
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for tick converters.
 */
@Slf4j
public abstract class AbstractTickConverter implements TickConverter {
  private OffsetProvider inputOffsetProvider;
  private RuntimeTick inputTick;
  private RuntimeTick outputTick;

  final void setOutputTick(RuntimeTick outputTick) {
    this.outputTick = outputTick;
  }

  final void setInputTick(RuntimeTick inputTick) {
    this.inputTick = inputTick;
  }

  final void setInputOffsetProvider(OffsetProvider inputOffsetProvider) {
    this.inputOffsetProvider = inputOffsetProvider;
  }

  @Override
  public RuntimeTick getOutputTick() {
    return outputTick;
  }

  RuntimeTick getInputTick() {
    return inputTick;
  }

  private long getInputOffset() {
    return inputOffsetProvider.getOffset();
  }

  @Override
  public long convert(long time) {
    LOG.trace("Converting {} from {} to {}", time, inputTick, outputTick);
    return convertTimeUnit(getInputOffset() + time);
  }

  @Override
  public long convertDuration(long time, long duration) {
    LOG.trace("Converting duration {} from {} to {}", time, inputTick, outputTick);
    return convert(time + duration) - convert(time);
  }

  long scaleTime(long time) {
    return time * inputTick.getNumerator() * outputTick.getDenominator()
        / (inputTick.getDenominator() * outputTick.getNumerator());
  }

  private long convertTimeUnit(long time) {
    switch (getOutputTick().getTimeUnit()) {
      case BEAT:
        return getTimeUnitAsBeat(time);
      case MICROSECOND:
        return getTimeUnitAsMicrosecond(time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  protected abstract long getTimeUnitAsBeat(long time);

  protected abstract long getTimeUnitAsMicrosecond(long time);

  protected long throwUnexpectedTimeUnit() {
    throw new OdinRuntimeException("Unexpected time unit " + getInputTick().getTimeUnit() + ":"
        + getOutputTick().getTimeUnit());
  }

}
