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

import static com.purplepip.odin.math.Wholes.EIGHT;
import static com.purplepip.odin.math.Wholes.FIVE;
import static com.purplepip.odin.math.Wholes.FOUR;
import static com.purplepip.odin.math.Wholes.NINE;
import static com.purplepip.odin.math.Wholes.ONE;
import static com.purplepip.odin.math.Wholes.SEVEN;
import static com.purplepip.odin.math.Wholes.SIX;
import static com.purplepip.odin.math.Wholes.THREE;
import static com.purplepip.odin.math.Wholes.TWO;
import static com.purplepip.odin.math.Wholes.ZERO;

public class Whole extends Rational {
  private static Whole[] WHOLES_CACHE = new Whole[] {
      ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE
  };
  /*
   * Local property for numerator for direct access.
   */
  private long numerator;

  public Whole(long numerator) {
    super(numerator, 1);
    this.numerator = numerator;
  }

  /**
   * Return whole for the given integer.
   *
   * @param integer integer value
   * @return whole object
   */
  public static Whole valueOf(long integer) {
    if (integer < 10 && integer > -1) {
      return WHOLES_CACHE[(int) integer];
    }
    return new Whole(integer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real plus(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return plus(whole);
    }
    return super.plus(real);
  }

  public Whole plus(Whole whole) {
    return Whole.valueOf(numerator + whole.numerator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real minus(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return minus(whole);
    }
    return super.minus(real);
  }

  public Whole minus(Whole whole) {
    return Whole.valueOf(numerator - whole.numerator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real times(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return times(whole);
    }
    return super.times(real);
  }

  public Whole times(Whole whole) {
    return Whole.valueOf(numerator * whole.numerator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real divide(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return divide(whole);
    }
    return super.divide(real);
  }

  public Rational divide(Whole whole) {
    return Rational.valueOf(numerator, whole.numerator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real modulo(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return modulo(whole);
    }
    return super.modulo(real);
  }

  public Whole modulo(Whole whole) {
    return Whole.valueOf(numerator % whole.numerator);
  }

  @Override
  public Whole wholeFloor() {
    return this;
  }

  @Override
  public Whole wholeCeiling() {
    return this;
  }

  @Override
  public long floor() {
    return numerator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real floor(Real radix) {
    if (radix instanceof Whole) {
      Whole whole = (Whole) radix;
      return floor(whole);
    }
    return super.floor(radix);
  }

  public Whole floor(Whole radix) {
    return Whole.valueOf(numerator - (numerator % radix.numerator));
  }

  @Override
  public Whole absolute() {
    if (isNegative()) {
      return Whole.valueOf(-numerator);
    }
    return this;
  }

  @Override
  public long nextFloor() {
    return numerator + 1;
  }

  @Override
  public Rational negative() {
    return Whole.valueOf(-numerator);
  }

  @Override
  public boolean isNegative() {
    return numerator < 0;
  }

  @Override
  public String toString() {
    return String.valueOf(numerator);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Whole whole = (Whole) o;

    return numerator == whole.numerator;
  }

  @Override
  public int hashCode() {
    return (int) (numerator ^ (numerator >>> 32));
  }

  /**
   * Calculate the smallest integer (closest to negative infinity) greater than or equal to this
   * whole number.
   *
   * @return ceiling value
   */
  @Override
  public long ceiling() {
    return numerator;
  }

  @Override
  public double getValue() {
    return numerator;
  }
}
