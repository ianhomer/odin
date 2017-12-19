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
class ConcreteReal implements Real {
  private static final double DOUBLE_PRECISION = 0.00000000000000000000001;
  private double value;

  ConcreteReal() {
  }

  public ConcreteReal(double value) {
    this.value = value;
  }

  /**
   * Get value as double.
   *
   * @return value as double
   */
  public double getValue() {
    return value;
  }

  /**
   * Add real number.
   *
   * @param real real number to add
   * @return result of addition
   */
  @Override
  public Real plus(Real real) {
    return Reals.valueOf(getValue() + real.getValue());
  }

  /**
   * Minus a real number.
   *
   * @param real real number to minus
   * @return result of subtraction
   */
  @Override
  public Real minus(Real real) {
    return Reals.valueOf(getValue() - real.getValue());
  }

  /**
   * Multiply a real number.
   *
   * @param real real number to multiply
   * @return result of multiplication
   */
  @Override
  public Real times(Real real) {
    return Reals.valueOf(getValue() * real.getValue());
  }

  /**
   * Divide a real number.
   *
   * @param real real number to divide
   * @return result of division
   */
  @Override
  public Real divide(Real real) {
    return Reals.valueOf(getValue() / real.getValue());
  }

  /**
   * Calculate modulo.
   *
   * @param real real number
   * @return modulo result
   */
  @Override
  public Real modulo(Real real) {
    return Reals.valueOf(getValue() % real.getValue());
  }



  @Override
  public ConcreteReal getLimit() {
    return this;
  }

  /**
   * Calculate the smallest integer (closest to negative infinity) greater than or equal to this
   * real number.
   *
   * @return ceiling value
   */
  @Override
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
  @Override
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
  @Override
  public Real floor(Real radix) {
    double flooredValue = getValue() - (getValue() % radix.getValue());
    if (radix instanceof Whole) {
      /*
       * Flooring with whole radix will always give a whole number, so lets be explicit about it.
       */
      return Wholes.valueOf((long) flooredValue);
    }
    return Reals.valueOf(flooredValue);
  }

  /**
   * Calculate the nearest multiple of base less than this rational number.
   *
   * @param radix radix
   * @return floored value
   */
  public Rational floor(Rational radix) {
    long multiples = (long) (value / radix.getValue());
    return radix.times(Wholes.valueOf(multiples));
  }

  /**
   * Calculate the largest whole number less than or equal to this real number.
   *
   * @return floored value
   */
  @Override
  public Whole wholeFloor() {
    return Wholes.valueOf(floor());
  }

  /**
   * Calculate the largest whole number greater than or equal to this real number.
   *
   * @return floored value
   */
  @Override
  public Whole wholeCeiling() {
    return Wholes.valueOf(ceiling());
  }


  /**
   * Calculate the smallest whole number greater than this real number.  Note that this function is
   * NOT the standard "ceil" function since this function will return the next whole number for
   * a whole number.
   *
   * @return next ceiling.
   */
  @Override
  public Whole nextWholeFloor() {
    return Wholes.valueOf(nextFloor());
  }

  /**
   * Calculate the absolute of this number.
   *
   * @return absolute of this number
   */
  @Override
  public Real absolute() {
    if (isNegative()) {
      return Reals.valueOf(-getValue());
    }
    return this;
  }

  /**
   * Return negative of this number.
   *
   * @return negative of this number
   */
  @Override
  public Real negative() {
    return Reals.valueOf(-getValue());
  }

  @JsonIgnore
  @Override
  public boolean isNegative() {
    return getValue() < 0;
  }

  @JsonIgnore
  @Override
  public boolean isPositive() {
    return getValue() > 0;
  }

  /**
   * Convert this to a rational number.
   *
   * @return rational version of this number
   */
  @Override
  public Rational toRational() {
    if (this instanceof Rational) {
      return (Rational) this;
    }
    LOG.warn("Cheap flooring of {} to make it a rational", this);
    /*
     * Very cheap flooring of non-rationals which is good enough for purpose.
     */
    return Wholes.valueOf(this.floor());
  }

  @Override
  public boolean ge(Real real) {
    return getValue() >= real.getValue();
  }

  @Override
  public boolean gt(Real real) {
    return getValue() > real.getValue();
  }

  @Override
  public boolean lt(Real real) {
    return getValue() < real.getValue();
  }

  @Override
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

    ConcreteReal real = (ConcreteReal) o;

    return Double.compare(real.value, value) == 0;
  }

  @Override
  public int hashCode() {
    long temp = Double.doubleToLongBits(value);
    return (int) (temp ^ (temp >>> 32));
  }

  @Override
  public String toString() {
    return String.valueOf(getValue());
  }
}
