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

/**
 * Default runtime tick.
 */
public class DefaultRuntimeTick implements RuntimeTick {
  private Tick underlyingTick;
  private double factor;
  private int factorAsInt;

  /**
   * Create a default runtime tick.
   *
   * @param tick underlying tick to base this runtime tick off
   */
  public DefaultRuntimeTick(Tick tick) {
    underlyingTick = tick;
    this.factor = (double) tick.getNumerator() / (double) tick.getDenominator();
    this.factorAsInt = tick.getNumerator() / tick.getDenominator();
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

  @Override
  public double getFactor() {
    return factor;
  }

  @Override
  public int getFactorAsInt() {
    return factorAsInt;
  }

  @Override public String toString() {
    return DefaultRuntimeTick.class.getSimpleName()
        + "(" + this.getNumerator() + "/" + this.getDenominator() + " " + this.getTimeUnit() + ")";
  }
}
