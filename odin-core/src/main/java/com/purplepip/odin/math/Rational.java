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

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Rational extends Real {
  private long numerator;
  private long denominator;
  private static final Map<Rational, Character> fractionCharacters = new HashMap<>();

  static {
    fractionCharacters.put(new Rational(1,2), '½');
    fractionCharacters.put(new Rational(1,4), '¼');
    fractionCharacters.put(new Rational(3,4), '¾');
    fractionCharacters.put(new Rational(1,3), '⅓');
    fractionCharacters.put(new Rational(2,3), '⅔');
    fractionCharacters.put(new Rational(1,5), '⅕');
    fractionCharacters.put(new Rational(2,5), '⅖');
    fractionCharacters.put(new Rational(3,5), '⅗');
    fractionCharacters.put(new Rational(4,5), '⅘');
    fractionCharacters.put(new Rational(1,6), '⅙');
    fractionCharacters.put(new Rational(5,6), '⅚');
    fractionCharacters.put(new Rational(1,8), '⅛');
    fractionCharacters.put(new Rational(3,8), '⅜');
    fractionCharacters.put(new Rational(5,8), '⅝');
    fractionCharacters.put(new Rational(7,8), '⅞');
  }

  /**
   * Create whole number.
   *
   * @param numerator numerator
   */
  public Rational(long numerator) {
    /*
     * Note that there is no need for simplification since denominator is 1.
     */
    this(numerator, 1, false);
  }

  /**
   * Create rational number with fraction simplification.
   *
   * @param numerator numerator
   * @param denominator denominator
   */
  public Rational(long numerator, long denominator) {
    this(numerator, denominator, true);
  }

  /**
   * Create rational number.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @param simplify whether to simplify the fraction
   */
  public Rational(long numerator, long denominator, boolean simplify) {
    this.numerator = numerator;
    this.denominator = denominator;
    if (simplify) {
      simplify();
    }
  }

  public long getNumerator() {
    return numerator;
  }

  public long getDenominator() {
    return denominator;
  }

  private void simplify() {
    for (long i = 2; i <= denominator / i; i++) {
      while (denominator % i == 0 && numerator % i == 0) {
        denominator /= i;
        numerator /= i;
      }
    }
  }

  @Override
  public double calculateValue() {
    return (double) numerator / denominator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real plus(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Real.valueOf(numerator * rational.denominator
              + rational.getNumerator() * getDenominator(),
          denominator * rational.denominator);
    }
    return super.plus(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real minus(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Real.valueOf(numerator * rational.getDenominator()
              - rational.getNumerator() * getDenominator(),
          denominator * rational.denominator);
    }
    return super.minus(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real times(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Real.valueOf(numerator * rational.getNumerator(),
          denominator * rational.getDenominator());
    }
    return super.times(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real divide(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Real.valueOf(numerator * rational.getDenominator(),
          denominator * rational.getNumerator());
    }
    return super.divide(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real modulo(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Real.valueOf((numerator * rational.getDenominator())
              % (rational.getNumerator() * denominator),
          rational.getDenominator() * denominator);
    }
    return super.modulo(real);
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
      long product1 = numerator * rational.getDenominator();
      long product2 = rational.getNumerator() * denominator;
      long product3 = denominator * rational.getDenominator();
      return Real.valueOf(product1 - (product1 % product2), product3);
    }
    return super.floor(radix);
  }

  @Override
  public String toString() {
    if (numerator == 0) {
      return "0";
    }
    StringBuilder builder = new StringBuilder(8);
    long floor = floor();
    if (floor > 0) {
      builder.append(floor());
    }
    long partNumerator = numerator % denominator;
    if (partNumerator > 0) {
      Rational part = new Rational(partNumerator, denominator, true);
      LOG.debug("Rational part = {}", part);
      Character special = fractionCharacters.get(part);
      if (special != null) {
        builder.append(special);
      } else {
        if (floor > 0) {
          builder.append('+');
        }
        builder.append(partNumerator).append('/').append(denominator);
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

    return numerator == rational.numerator && denominator == rational.denominator;
  }

  @Override
  public int hashCode() {
    int result = (int) (numerator ^ (numerator >>> 32));
    result = 31 * result + (int) (denominator ^ (denominator >>> 32));
    return result;
  }
}
