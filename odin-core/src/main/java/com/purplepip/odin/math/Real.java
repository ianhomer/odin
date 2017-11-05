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

package com.purplepip.odin.math;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

/**
 * A real number.  Note that the Rational and Whole classes extend this Real class and
 * where possible maintain the precision of Rational and Whole via operation calls.  This
 * allows operations applied between two Rationals to remain a Rational, however an operation
 * between a Rational and Real will become a Real.  This allows sequences such as music and
 * rhythm to retain precision such as triplets through tick conversions.
 */
@Slf4j
public class Real implements Bound {
  private static final double DOUBLE_PRECISION = 0.00000000000000000000001;
  private double value;
  private boolean valueCalculated;

  Real() {
  }

  public Real(double value) {
    this.value = value;
  }

  /**
   * Create Whole number.  Please use the Whole.valueOf method.  This only exists
   * to prevent auto use of the valueOf with a double argument when an long is passed in.
   *
   * @param integer integer to create the whole number from
   * @return whole number
   */
  public static Whole valueOf(long integer) {
    StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
    LOG.warn("Please call 'Whole.valueOf({})' not 'Real.valueOf({})' "
        + "in {} @ line {} "
        + "given you know at compile time you what a whole number", integer, integer,
        stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
    return Whole.valueOf(integer);
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

  /**
   * Calculate real value from string.
   *
   * @param value real as string
   * @return real value
   */
  public static Real valueOf(String value) {
    if (value == null || value.indexOf('.') < 0) {
      return Rational.valueOf(value);
    }
    return valueOf(Double.valueOf(value));
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



  @Override
  public Real getLimit() {
    return this;
  }

  /**
   * Calculate the smallest integer (closest to negative infinity) greater than or equal to this
   * real number.
   *
   * @return ceiling value
   */
  public long ceiling() {
    if (isNegative()) {
      return (long) getValue();
    } else {
      return ((getValue() % 1 < DOUBLE_PRECISION) ? 0 : 1) + (long) getValue();
    }
  }


  /**
   * Calculate the smallest whole number greater than this real number.  Note that this function is
   * NOT the standard "ceil" function since this function will return the next whole number for
   * a whole number.
   *
   * @return next ceiling.
   */
  public long nextFloor() {
    return floor() + 1;
  }

  /**
   * Calculate the largest integer (closest to positive infinity) less than or equal to this
   * real number.
   *
   * @return floored value
   */
  public long floor() {
    if (isNegative()) {
      return (long) getValue() - 1;
    } else {
      return (long) getValue();
    }
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
      return Whole.valueOf((long) flooredValue);
    }
    return Real.valueOf(flooredValue);
  }

  /**
   * Calculate the largest whole number less than or equal to this real number.
   *
   * @return floored value
   */
  public Whole wholeFloor() {
    return Whole.valueOf(floor());
  }

  /**
   * Calculate the smallest whole number greater than this real number.  Note that this function is
   * NOT the standard "ceil" function since this function will return the next whole number for
   * a whole number.
   *
   * @return next ceiling.
   */
  public Whole nextWholeFloor() {
    return Whole.valueOf(nextFloor());
  }

  /**
   * Calculate the absolute of this number.
   *
   * @return absolute of this number
   */
  public Real absolute() {
    if (isNegative()) {
      return Real.valueOf(-getValue());
    }
    return this;
  }

  /**
   * Return negative of this number.
   *
   * @return negative of this number
   */
  public Real negative() {
    return Real.valueOf(-getValue());
  }

  @JsonIgnore
  public boolean isNegative() {
    return getValue() < 0;
  }

  @JsonIgnore
  public boolean isPositive() {
    return getValue() > 0;
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
    LOG.warn("Cheap flooring of {} to make it a rational", this);
    /*
     * Very cheap flooring of non-rationals which is good enough for purpose.
     */
    return Whole.valueOf(this.floor());
  }

  public boolean ge(Real real) {
    return getValue() >= real.getValue();
  }

  public boolean gt(Real real) {
    return getValue() > real.getValue();
  }

  public boolean lt(Real real) {
    return getValue() < real.getValue();
  }

  public boolean le(Real real) {
    return getValue() <= real.getValue();
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
