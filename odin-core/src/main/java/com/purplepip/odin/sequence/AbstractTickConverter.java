/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.sequence;

import com.purplepip.odin.common.OdinRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for tick converters.
 */
public abstract class AbstractTickConverter implements TickConverter {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultTickConverter.class);

  private long inputOffset;
  private RuntimeTick inputTick;
  private RuntimeTick outputTick;

  void setOutputTick(RuntimeTick outputTick) {
    this.outputTick = outputTick;
  }

  void setInputTick(RuntimeTick inputTick) {
    this.inputTick = inputTick;
  }

  void setInputOffset(long inputOffset) {
    this.inputOffset = inputOffset;
  }

  @Override
  public RuntimeTick getOutputTick() {
    return outputTick;
  }

  RuntimeTick getInputTick() {
    return inputTick;
  }

  protected long getInputOffset() {
    return inputOffset;
  }

  @Override
  public long convert(long time) {
    LOG.trace("Converting {} from {} to {}", time, inputTick, outputTick);
    return convertTimeUnit(inputOffset + time);
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
