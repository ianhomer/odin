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

public class ConcreteWhole extends AbstractRational implements Whole {
  /*
   * Local property for numerator for direct access.
   */
  private final long numerator;

  public ConcreteWhole(long numerator) {
    this.numerator = numerator;
  }

  @Override
  public long getNumerator() {
    return numerator;
  }

  @Override
  public long getDenominator() {
    return 1;
  }

  @Override
  public boolean isSimplified() {
    return true;
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

  @Override
  public Whole plus(Whole whole) {
    return Wholes.valueOf(numerator + whole.getNumerator());
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

  @Override
  public Whole minus(Whole whole) {
    return Wholes.valueOf(numerator - whole.getNumerator());
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

  @Override
  public Whole times(Whole whole) {
    return Wholes.valueOf(numerator * whole.getNumerator());
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

  @Override
  public Rational divide(Whole whole) {
    return Rationals.valueOf(numerator, whole.getNumerator());
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

  @Override
  public Whole modulo(Whole whole) {
    return Wholes.valueOf(numerator % whole.getNumerator());
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

  @Override
  public Whole floor(Whole radix) {
    return Wholes.valueOf(numerator - (numerator % radix.getNumerator()));
  }

  @Override
  public Whole absolute() {
    if (isNegative()) {
      return Wholes.valueOf(-numerator);
    }
    return this;
  }

  @Override
  public long nextFloor() {
    return numerator + 1;
  }

  @Override
  public Whole negative() {
    return Wholes.valueOf(-numerator);
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

    return numerator == whole.getNumerator();
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

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean lt(Real real) {
    if (real instanceof Whole) {
      return lt((Whole) real);
    }
    return super.lt(real);
  }

  public boolean lt(Whole whole) {
    return numerator < whole.getNumerator();
  }

  @Override
  public Whole toRational() {
    return this;
  }

  @Override
  public Whole getLimit() {
    return this;
  }

  @Override
  public boolean isPositive() {
    return numerator > 0;
  }
}
