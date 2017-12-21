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
class ConcreteReal extends AbstractReal {
  private static final double DOUBLE_PRECISION = 0.00000000000000000000001;
  private double value;

  ConcreteReal() {
  }

  public ConcreteReal(double value) {
    this.value = value;
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

  @Override
  public Real plus(Rational rational) {
    return Reals.valueOf(getValue() + rational.getValue());
  }

  @Override
  public Real plus(Whole whole) {
    return Reals.valueOf(getValue() + whole.getNumerator());
  }

  @Override
  public Real minus(Real real) {
    return Reals.valueOf(getValue() - real.getValue());
  }

  @Override
  public Real minus(Rational rational) {
    return Reals.valueOf(getValue() - rational.getValue());
  }

  @Override
  public Real minus(Whole whole) {
    return Reals.valueOf(getValue() - whole.getNumerator());
  }


  /**
   * Get value as double.
   *
   * @return value as double
   */
  @Override
  public double getValue() {
    return value;
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
  @Override
  public long ceiling() {
    if (isNegative()) {
      return (long) getValue();
    } else {
      return ((getValue() % 1 < DOUBLE_PRECISION) ? 0 : 1) + (long) getValue();
    }
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

  @JsonIgnore
  @Override
  public boolean isZero() {
    return getValue() == 0;
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
  public Real asMutable() {
    // TODO : Implement mutable real
    return this;
  }

  @Override
  public Real asImmutable() {
    return this;
  }

  @Override
  public String toString() {
    return String.valueOf(getValue());
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
}
