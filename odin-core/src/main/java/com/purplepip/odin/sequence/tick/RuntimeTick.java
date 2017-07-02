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

/**
 * Runtime tick with extra methods used by runtime.
 */
public class RuntimeTick implements Tick {
  private double factor;
  private Tick underlyingTick;

  /**
   * Create new runtime tick.
   *
   * @param tick underlying tick to base this on
   */
  public RuntimeTick(Tick tick) {
    underlyingTick = tick;
    factor = (double) getNumerator() / (double) getDenominator();
  }

  @Override
  public TimeUnit getTimeUnit() {
    return underlyingTick.getTimeUnit();
  }

  @Override
  public int getNumerator() {
    return underlyingTick.getNumerator();
  }

  @Override
  public int getDenominator() {
    return underlyingTick.getDenominator();
  }

  public double getFactor() {
    return factor;
  }
}
