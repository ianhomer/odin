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

package com.purplepip.odin.clock.tick;

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Wholes;

/**
 * Default tick implementation.
 */
public class DefaultTick implements Tick {
  private final TimeUnit timeUnit;
  private final Rational factor;

  public DefaultTick(TimeUnit timeUnit) {
    this(timeUnit, 1);
  }

  public DefaultTick(TimeUnit timeUnit, int numerator) {
    this(timeUnit, Wholes.valueOf(numerator));
  }

  public DefaultTick(Tick tick) {
    this(tick.getTimeUnit(), tick.getFactor());
  }

  /**
   * Create a tick.
   *
   * @param timeUnit time unit
   * @param factor factor
   */
  public DefaultTick(TimeUnit timeUnit, Rational factor) {
    this.timeUnit = timeUnit;
    this.factor = factor;
  }

  @Override
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  @Override
  public Rational getFactor() {
    return factor;
  }

  public String toString() {
    return this.getTimeUnit()
        + (this.getFactor().equals(Wholes.ONE) ? "" : "x" + this.getFactor());
  }
}
