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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConcreteRational extends AbstractRational {
  private final long numerator;
  private final long denominator;
  private final boolean simplified;
  private static final Map<Rational, Character> fractionCharacters = new HashMap<>();
  private double value;
  private boolean valueCalculated;

  static {
    fractionCharacters.put(Rationals.valueOf(1,2), '½');
    fractionCharacters.put(Rationals.valueOf(1,4), '¼');
    fractionCharacters.put(Rationals.valueOf(3,4), '¾');
    fractionCharacters.put(Rationals.valueOf(1,3), '⅓');
    fractionCharacters.put(Rationals.valueOf(2,3), '⅔');
    fractionCharacters.put(Rationals.valueOf(1,5), '⅕');
    fractionCharacters.put(Rationals.valueOf(2,5), '⅖');
    fractionCharacters.put(Rationals.valueOf(3,5), '⅗');
    fractionCharacters.put(Rationals.valueOf(4,5), '⅘');
    fractionCharacters.put(Rationals.valueOf(1,6), '⅙');
    fractionCharacters.put(Rationals.valueOf(5,6), '⅚');
    fractionCharacters.put(Rationals.valueOf(1,8), '⅛');
    fractionCharacters.put(Rationals.valueOf(3,8), '⅜');
    fractionCharacters.put(Rationals.valueOf(5,8), '⅝');
    fractionCharacters.put(Rationals.valueOf(7,8), '⅞');
  }

  /**
   * Create rational number with fraction simplification.
   *
   * @param numerator numerator
   * @param denominator denominator
   */
  private ConcreteRational(long numerator, long denominator) {
    this(numerator, denominator, false);
  }

  /**
   * Create rational number.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @param simplified whether rational was simplified
   */
  protected ConcreteRational(long numerator, long denominator, boolean simplified) {
    this.numerator = numerator;
    this.denominator = denominator;
    this.simplified = simplified;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real plus(Real real) {
    return real.plus(this);
  }

  @Override
  public Rational plus(Rational rational) {
    return Rationals.valueOf(getNumerator() * rational.getDenominator()
            + rational.getNumerator() * getDenominator(),
        getDenominator() * rational.getDenominator(), isSimplified());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Rational plus(Whole whole) {
    return Rationals.valueOf(
        numerator + getDenominator() * whole.getNumerator(), getDenominator());
  }

  @Override
  public Real minus(Real real) {
    return real.negative().plus(this);
  }

  @Override
  public Rational minus(Rational rational) {
    return Rationals.valueOf(getNumerator() * rational.getDenominator()
            - rational.getNumerator() * getDenominator(),
        getDenominator() * rational.getDenominator(), isSimplified());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Rational minus(Whole whole) {
    return Rationals.valueOf(
        numerator - getDenominator() * whole.getNumerator(), getDenominator());
  }

  @Override
  public long getNumerator() {
    return numerator;
  }

  @Override
  public long getDenominator() {
    return denominator;
  }

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Override
  public boolean isSimplified() {
    return simplified;
  }

  protected double calculateValue() {
    return (double) numerator / denominator;
  }

  @Override
  public double getValue() {
    if (!valueCalculated) {
      value = calculateValue();
      valueCalculated = true;
    }
    return value;
  }


  @Override
  public boolean isPositive() {
    return numerator > 0 == denominator > 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long floor() {
    return numerator / denominator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real floor(Real radix) {
    if (radix instanceof Rational) {
      Rational rational = (Rational) radix;
      return floor(rational);
    }
    return super.floor(radix);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long ceiling() {
    if (isNegative()) {
      return numerator / denominator;
    } else if (numerator % denominator == 0) {
      return numerator / denominator;
    } else {
      return 1 + (numerator / denominator);
    }
  }

  @Override
  public Rational toRational() {
    return this;
  }

  @Override
  public Rational getLimit() {
    return this;
  }

  @Override
  public Rational absolute() {
    if (numerator < 0) {
      if (denominator < 0) {
        return Rationals.valueOf(-numerator, -denominator, simplified);
      } else {
        return Rationals.valueOf(-numerator, denominator, simplified);
      }
    } else if (denominator < 0) {
      return Rationals.valueOf(numerator, -denominator, simplified);
    } else {
      return this;
    }
  }

  @Override
  public Rational negative() {
    return Rationals.valueOf(-numerator, denominator, simplified);
  }

  @Override
  public boolean isNegative() {
    return (numerator < 0) ^ (denominator < 0);
  }

  @Override
  public String toString() {
    if (numerator == 0) {
      return "0";
    }
    StringBuilder builder = new StringBuilder(8);

    /*
     * Normalise rational to an absolute number and append "-" to start of string if rational
     * was negative.
     */
    if (this.isNegative()) {
      builder.append('-');
    }
    Rational absolute = this.absolute();

    /*
     * Write out the integer part if rational was simplified.
     */
    long floor;
    long partNumerator;
    if (simplified) {
      floor = absolute.floor();
      if (floor > 0) {
        builder.append(floor);
      }
      partNumerator = absolute.getNumerator() % absolute.getDenominator();
    } else {
      /*
       * If fraction was not simplified then we write as it is and do not pull out the integer
       * part.  This is useful for writing musical time signatures.
       */
      floor = 0;
      partNumerator = absolute.getNumerator();
    }

    /*
     * Write out fraction part.
     */
    if (partNumerator != 0) {
      Rational part = Rationals.valueOf(partNumerator, absolute.getDenominator(), simplified);
      Character special = fractionCharacters.get(part);
      if (special != null) {
        builder.append(special);
      } else {
        if (floor > 0) {
          builder.append('+');
        }
        builder.append(partNumerator).append('/').append(absolute.getDenominator());
      }
    }
    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Rational rational = (Rational) o;

    return numerator == rational.getNumerator() && denominator == rational.getDenominator();
  }

  @Override
  public int hashCode() {
    int result = (int) (numerator ^ (numerator >>> 32));
    result = 31 * result + (int) (denominator ^ (denominator >>> 32));
    return result;
  }
}
