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

public class Whole extends Rational {
  public Whole(long numerator) {
    super(numerator, 1);
  }

  public static Whole valueOf(long integer) {
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
    return Whole.valueOf(getNumerator() + whole.getNumerator());
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
    return Whole.valueOf(getNumerator() - whole.getNumerator());
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
    return Whole.valueOf(getNumerator() * whole.getNumerator());
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
    return Rational.valueOf(getNumerator(), whole.getNumerator());
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
    return Whole.valueOf(getNumerator() % whole.getNumerator());
  }

  @Override
  public Whole wholeFloor() {
    return this;
  }

  @Override
  public long floor() {
    return getNumerator();
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
    return Whole.valueOf(getNumerator() - (getNumerator() % radix.getNumerator()));
  }

  @Override
  public Whole absolute() {
    if (isNegative()) {
      return Whole.valueOf(-getNumerator());
    }
    return this;
  }

  @Override
  public long nextFloor() {
    return getNumerator() + 1;
  }

  @Override
  public Rational negative() {
    return Whole.valueOf(-getNumerator());
  }

  @Override
  public boolean isNegative() {
    return getNumerator() < 0;
  }

  @Override
  public String toString() {
    return String.valueOf(getNumerator());
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

    return getNumerator() == whole.getNumerator();
  }

  @Override
  public int hashCode() {
    return (int) (getNumerator() ^ (getNumerator() >>> 32));
  }
}
