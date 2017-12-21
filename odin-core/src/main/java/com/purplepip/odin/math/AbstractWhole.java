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

public abstract class AbstractWhole extends AbstractRational implements Whole {
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
    return real.plus(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Rational plus(Rational rational) {
    return rational.plus(this);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Real minus(Real real) {
    if (real instanceof Whole) {
      return minus((Whole) real);
    } else if (real instanceof Rational) {
      return minus((Rational) real);
    }
    return Reals.valueOf(getNumerator() - real.getValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Rational minus(Rational rational) {
    if (rational instanceof Whole) {
      return minus((Whole) rational);
    }
    return Rationals.valueOf(
        getNumerator() * rational.getDenominator() - rational.getNumerator(),
        rational.getDenominator());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real times(Real real) {
    if (real instanceof Whole) {
      return Wholes.valueOf(getNumerator() * ((Whole) real).getNumerator());
    } else if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Rationals.valueOf(getNumerator() * rational.getNumerator(),
          rational.getDenominator());
    }
    return Reals.valueOf(getNumerator() * real.getValue());
  }

  @Override
  public Whole times(Whole whole) {
    return Wholes.valueOf(getNumerator() * whole.getNumerator());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real divide(Real real) {
    if (real instanceof Whole) {
      return Rationals.valueOf(getNumerator(), ((Whole) real).getNumerator());
    } else if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Rationals.valueOf(getNumerator() * rational.getDenominator(),
          rational.getNumerator());
    }
    return Reals.valueOf(getNumerator() / real.getValue());
  }

  @Override
  public Rational divide(Whole whole) {
    return Rationals.valueOf(getNumerator(), whole.getNumerator());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real modulo(Real real) {
    if (real instanceof Whole) {
      return Wholes.valueOf(getNumerator() % ((Whole) real).getNumerator());
    } else if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Rationals.valueOf((getNumerator() * rational.getDenominator())
              % rational.getNumerator(), rational.getDenominator());
    }
    return Reals.valueOf(getNumerator() % real.getValue());
  }

  @Override
  public Whole modulo(Whole whole) {
    return Wholes.valueOf(getNumerator() % whole.getNumerator());
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

  @Override
  public Whole floor(Whole radix) {
    return Wholes.valueOf(getNumerator() - (getNumerator() % radix.getNumerator()));
  }

  @Override
  public Whole absolute() {
    if (isNegative()) {
      return Wholes.valueOf(-getNumerator());
    }
    return this;
  }

  @Override
  public long nextFloor() {
    return getNumerator() + 1;
  }

  @Override
  public Whole negative() {
    return Wholes.valueOf(-getNumerator());
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

  /**
   * Calculate the smallest integer (closest to negative infinity) greater than or equal to this
   * whole number.
   *
   * @return ceiling value
   */
  @Override
  public long ceiling() {
    return getNumerator();
  }

  @Override
  public double getValue() {
    return getNumerator();
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
    return getNumerator() < whole.getNumerator();
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
    return getNumerator() > 0;
  }
}
