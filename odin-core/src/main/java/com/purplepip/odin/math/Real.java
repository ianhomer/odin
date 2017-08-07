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
 * A real number.  Note that the Rational and Whole classes extend this Real class and
 * where possible maintain the precision of Rational and Whole via operation calls.  This
 * allows operations applied between two Rationals to remain a Rational, however an operation
 * between a Rational and Real will become a Real.  This allows sequences such as music and
 * rhythm to retain precision such as triplets through tick conversions.
 */
public class Real {
  private double value;
  private boolean valueCalculated = false;

  public static Whole valueOf(long numerator) {
    return new Whole(numerator);
  }

  /**
   * Create a rational number from a given numerator and denominator.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @return rational number
   */
  public static Rational valueOf(long numerator, long denominator) {
    if (numerator == 0) {
      return new Whole(0);
    } else if (numerator % denominator == 0) {
      return new Whole(numerator / denominator);
    }
    return new Rational(numerator, denominator);
  }

  /**
   * Create a real number from the given double.  This will return a whole number if the given
   * double is a whole number.
   *
   * @param value double value
   * @return real value
   */
  public static Real valueOf(double value) {
    if (value % 1 == 0) {
      return new Whole((long) value);
    }
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
    return Real.valueOf(getValue() + real.getValue());
  }

  /**
   * Minus a real number.
   *
   * @param real real number to minus
   * @return result of subtraction
   */
  public Real minus(Real real) {
    return Real.valueOf(getValue() - real.getValue());
  }

  /**
   * Multiply a real number.
   *
   * @param real real number to multiply
   * @return result of multiplication
   */
  public Real times(Real real) {
    return Real.valueOf(getValue() * real.getValue());
  }

  /**
   * Divide a real number.
   *
   * @param real real number to divide
   * @return result of division
   */
  public Real divide(Real real) {
    return Real.valueOf(getValue() / real.getValue());
  }

  /**
   * Calculate modulo.
   *
   * @param real real number
   * @return modulo result
   */
  public Real modulo(Real real) {
    return Real.valueOf(getValue() % real.getValue());
  }

  /**
   * Calculate the nearest integer less than this real number.
   *
   * @return floored value
   */
  public long floor() {
    return (long) getValue();
  }

  /**
   * Calculate the nearest multiple of base less than this real number.
   *
   * @param radix radix
   * @return floored value
   */
  public Real floor(Real radix) {
    return minus(modulo(radix));
  }

  public boolean ge(Real rational) {
    return getValue() >= rational.getValue();
  }

  public boolean gt(Real rational) {
    return getValue() > rational.getValue();
  }

  public boolean lt(Real rational) {
    return getValue() < rational.getValue();
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

  public String toString() {
    return String.valueOf(value);
  }
}
