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

package com.purplepip.odin.math;

/**
 * A real number.
 */
public class Real {
  private double value;
  private boolean valueCalculated = false;

  public static Rational valueOf(long numerator) {
    return new Rational(numerator, 1);
  }

  public static Real valueOf(double value) {
    return new Real(value);
  }

  Real() {
  }

  public Real(double value) {
    this.value = value;
  }

  public double calculateValue() {
    return value;
  }

  /**
   * Get value as double.
   *
   * @return value as double
   */
  public double getValue() {
    if (!valueCalculated) {
      value = calculateValue();
      valueCalculated = true;
    }
    return value;
  }

  /**
   * Add real number.
   *
   * @param real real number to add
   * @return result of addition
   */
  public Real plus(Real real) {
    return new Real(getValue() + real.getValue());
  }

  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Real real = (Real) o;

    return Double.compare(real.value, value) == 0;
  }

  @Override
  public int hashCode() {
    long temp = Double.doubleToLongBits(value);
    return (int) (temp ^ (temp >>> 32));
  }
}
