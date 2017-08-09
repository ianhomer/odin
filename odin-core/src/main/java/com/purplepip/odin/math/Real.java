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

import lombok.extern.slf4j.Slf4j;

/**
 * A real number.  Note that the Rational and Whole classes extend this Real class and
 * where possible maintain the precision of Rational and Whole via operation calls.  This
 * allows operations applied between two Rationals to remain a Rational, however an operation
 * between a Rational and Real will become a Real.  This allows sequences such as music and
 * rhythm to retain precision such as triplets through tick conversions.
 */
@Slf4j
public class Real {
  private double value;
  private boolean valueCalculated;

  Real() {
  }

  public Real(double value) {
    this.value = value;
  }

  public static Whole valueOf(long integer) {
    return new Whole(integer);
  }

  /**
   * Create a rational number from a given numerator and denominator.  Note a Whole number is
   * returned if the numerator is multiple of the denominator.
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
   * Create a real number from the given double.  This will ALWAYS return a real number using a
   * double underlying value.  This ensure that once a logic pathway chooses to go to double
   * arithmetic it will stay with double arithmetic.
   *
   *
   * @param value double value
   * @return real value
   */
  public static Real valueOf(double value) {
    return new Real(value);
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
    double flooredValue = getValue() - (getValue() % radix.getValue());
    if (radix instanceof Whole) {
      /*
       * Flooring with whole radix will always give a whole number, so lets be explicit about it.
       */
      return Real.valueOf((long) flooredValue);
    }
    return Real.valueOf(flooredValue);
  }

  /**
   * Convert this to a rational number.
   *
   * @return rational version of this number
   */
  public Rational toRational() {
    if (this instanceof Rational) {
      return (Rational) this;
    }
    /*
     * Very cheap flooring of non-rationals which is good enough for purpose.
     */
    return Real.valueOf(this.floor());
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

  public boolean le(Real rational) {
    return getValue() <= rational.getValue();
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

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
