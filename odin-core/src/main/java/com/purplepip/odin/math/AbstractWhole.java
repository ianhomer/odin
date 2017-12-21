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

import static com.purplepip.odin.math.Rationals.floorDenominator;
import static com.purplepip.odin.math.Rationals.floorNumerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.stream.Stream;

public abstract class AbstractWhole implements Whole {
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
      return times((Whole) real);
    } else if (real instanceof Rational) {
      return times((Rational) real);
    }
    return Reals.valueOf(getNumerator() * real.getValue());
  }

  @Override
  public Rational times(Rational rational) {
    return Rationals.valueOf(getNumerator() * rational.getNumerator(),
        rational.getDenominator());
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
      return divide((Whole) real);
    } else if (real instanceof Rational) {
      return divide((Rational) real);
    }
    return Reals.valueOf(getNumerator() / real.getValue());
  }

  @Override
  public Rational divide(Rational rational) {
    return Rationals.valueOf(getNumerator() * rational.getDenominator(),
        rational.getNumerator());
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
      return modulo((Whole) real);
    } else if (real instanceof Rational) {
      return modulo((Rational) real);
    }
    return Reals.valueOf(getNumerator() % real.getValue());
  }

  @Override
  public Rational modulo(Rational rational) {
    return Rationals.valueOf((getNumerator() * rational.getDenominator())
        % rational.getNumerator(), rational.getDenominator());
  }

  @Override
  public Whole modulo(Whole whole) {
    return Wholes.valueOf(getNumerator() % whole.getNumerator());
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
      return floor((Whole) radix);
    } else if (radix instanceof Rational) {
      return floor((Rational) radix);
    }
    return Reals.valueOf(Reals.floor(getValue(), radix.getValue()));
  }

  @Override
  public Rational floor(Rational radix) {
    return Rationals.valueOf(
        floorNumerator(getNumerator(), 1,
            radix.getNumerator(), radix.getDenominator()),
        floorDenominator(1, radix.getDenominator()), true);
  }

  @Override
  public Whole floor(Whole radix) {
    return Wholes.valueOf(getNumerator() - (getNumerator() % radix.getNumerator()));
  }

  @Override
  public Whole wholeFloor() {
    return this;
  }

  @Override
  public long nextFloor() {
    return getNumerator() + 1;
  }

  @Override
  public Whole nextWholeFloor() {
    return Wholes.valueOf(nextFloor());
  }

  @Override
  public Whole wholeCeiling() {
    return this;
  }

  @Override
  public Whole absolute() {
    if (isNegative()) {
      return Wholes.valueOf(-getNumerator());
    }
    return this;
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
   * {@inheritDoc}
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
    return real instanceof Whole ? lt((Whole) real) : getNumerator() < real.getValue();
  }

  public boolean lt(Whole whole) {
    return getNumerator() < whole.getNumerator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean ge(Real real) {
    return getNumerator() >= real.getValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean gt(Real real) {
    return getNumerator() > real.getValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean le(Real real) {
    return getNumerator() <= real.getValue();
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

  /**
   * {@inheritDoc}
   */
  @JsonIgnore
  @Override
  public Stream<Rational> getEgyptianFractions() {
    return getEgyptianFractions(1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Stream<Rational> getEgyptianFractions(int maxIntegerPart) {
    return Rationals.getEgyptianFractions(this, maxIntegerPart);
  }
}
