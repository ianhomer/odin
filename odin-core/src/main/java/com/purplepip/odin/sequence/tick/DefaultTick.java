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

package com.purplepip.odin.sequence.tick;

import com.purplepip.odin.sequence.TimeUnit;
import lombok.ToString;

/**
 * Default tick implementation.
 */
@ToString
public class DefaultTick implements Tick {
  private TimeUnit timeUnit;
  private int numerator;
  private int denominator;

  public DefaultTick(TimeUnit timeUnit) {
    this(timeUnit, 1);
  }

  public DefaultTick(TimeUnit timeUnit, int numerator) {
    this(timeUnit, numerator, 1);
  }

  public DefaultTick(Tick tick) {
    this(tick.getTimeUnit(), tick.getNumerator(), tick.getDenominator());
  }

  /**
   * Create a tick.
   *
   * @param timeUnit time unit
   * @param numerator numerator
   * @param denominator denominator
   */
  public DefaultTick(TimeUnit timeUnit, int numerator, int denominator) {
    this.timeUnit = timeUnit;
    this.numerator = numerator;
    this.denominator = denominator;
  }

  @Override
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  @Override
  public int getNumerator() {
    return numerator;
  }

  @Override
  public int getDenominator() {
    return denominator;
  }
}
