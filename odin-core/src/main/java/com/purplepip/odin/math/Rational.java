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

import com.purplepip.odin.common.OdinRuntimeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Rational extends Real {
  private static final int MAX_EGYPTIAN_FRACTIONS = 20;

  private long numerator;
  private long denominator;
  private boolean simplified;
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

  /**
   * Create a simplified rational number from a given numerator and denominator.  Note a Whole
   * number is returned if the numerator is multiple of the denominator.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @return simplified rational
   */
  public static Rational valueOf(long numerator, long denominator) {
    return Rational.valueOf(numerator, denominator, true);
  }

  /**
   * Create a rational number from a given numerator and denominator.  Note a Whole number is
   * returned if the numerator is multiple of the denominator.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @param simplified whether to simplify the rational number
   * @return rational number
   */
  public static Rational valueOf(long numerator, long denominator, boolean simplified) {
    if (numerator == 0) {
      return new Whole(0);
    } else if (numerator % denominator == 0) {
      return new Whole(numerator / denominator);
    }
    return new Rational(numerator, denominator, simplified);
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
    /*
     * Reduce two negatives to positives.
     */
    if (denominator < 0 && numerator < 0) {
      denominator = -denominator;
      numerator = -numerator;
    }
    simplified = true;
  }

  public boolean isSimplified() {
    return simplified;
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
      return plus(rational);
    }
    return super.plus(real);
  }

  /**
   * Add rational number to this rational number.
   *
   * @param rational rational number to add
   * @return result of addition
   */
  public Rational plus(Rational rational) {
    return Rational.valueOf(numerator * rational.denominator
            + rational.getNumerator() * getDenominator(),
        denominator * rational.denominator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real minus(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return minus(rational);
    }
    return super.minus(real);
  }

  /**
   * Subtraction when we know we have a rational number.
   *
   * @param rational rational number
   * @return result of subtraction
   */
  public Rational minus(Rational rational) {
    return Rational.valueOf(numerator * rational.getDenominator()
            - rational.getNumerator() * getDenominator(),
        denominator * rational.denominator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real times(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return Rational.valueOf(numerator * rational.getNumerator(),
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
      return Rational.valueOf(numerator * rational.getDenominator(),
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
      return modulo(rational);
    }
    return super.modulo(real);
  }

  /**
   * Calculate modulo of this rational number.
   *
   * @param rational radix
   * @return result of modulo operation
   */
  public Rational modulo(Rational rational) {
    return Rational.valueOf((numerator * rational.getDenominator())
            % (rational.getNumerator() * denominator),
        rational.getDenominator() * denominator);
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
   * Calculate floor from a value we know is rational.
   *
   * @param rational rational number
   * @return floored value
   */
  public Rational floor(Rational rational) {
    long product1 = numerator * rational.getDenominator();
    long product2 = rational.getNumerator() * denominator;
    long product3 = denominator * rational.getDenominator();
    return Rational.valueOf(product1 - (product1 % product2), product3);
  }

  @Override
  public Rational absolute() {
    if (numerator < 0) {
      if (denominator < 0) {
        return Rational.valueOf(-numerator, -denominator, simplified);
      } else {
        return Rational.valueOf(-numerator, denominator, simplified);
      }
    } else if (denominator < 0) {
      return Rational.valueOf(numerator, -denominator, simplified);
    } else {
      return this;
    }
  }

  @Override
  public Rational negative() {
    return Rational.valueOf(-numerator, denominator, simplified);
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
      Rational part = new Rational(partNumerator, absolute.getDenominator(), isSimplified());
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

  private void addEgyptianFractionPart(List<Rational> egyptianFractions,
                                       Rational part, boolean isNegative) {
    if (isNegative) {
      egyptianFractions.add(part.negative());
    } else {
      egyptianFractions.add(part);
    }
  }

  /**
   * Get egyptian fractions with integer part split into multiple ones.
   *
   * @return egyptian fractions
   */
  public Stream<Rational> getEgyptianFractions() {
    return getEgyptianFractions(1);
  }

  /**
   * Get egyptian fractions.
   *
   * @param maxIntegerPart Max integer pary
   * @return egyptian fractions
   */
  public Stream<Rational> getEgyptianFractions(int maxIntegerPart) {
    List<Rational> egyptianFractions = new ArrayList<>();
    Rational remainder = this;
    boolean isNegative = false;

    if (remainder.isNegative()) {
      remainder = remainder.absolute();
      isNegative = true;
    }

    int count = 0;

    /*
     * Split the integer part into multiple integers less than or equal to the max integer.
     */
    Whole maxWholePart = Whole.valueOf(maxIntegerPart);
    while (count < MAX_EGYPTIAN_FRACTIONS && remainder.ge(maxWholePart)) {
      remainder = remainder.minus(maxWholePart);
      addEgyptianFractionPart(egyptianFractions, maxWholePart, isNegative);
      count++;
    }

    /*
     * Split out the remaining integer less than the max integer part
     */
    if (remainder.gt(Wholes.ONE)) {
      Rational part = remainder.floor(Wholes.ONE);
      remainder = remainder.minus(part);
      count++;
      addEgyptianFractionPart(egyptianFractions, part, isNegative);
    }

    if (count >= MAX_EGYPTIAN_FRACTIONS) {
      throw new OdinRuntimeException(
          "Overflow of " + count
              + " when splitting out integer parts for " + this + ".  Remainder = " + remainder);
    }

    /*
     * Now split the fractions
     */
    int lastDenominator = 0;

    while (count < MAX_EGYPTIAN_FRACTIONS && remainder.gt(Wholes.ZERO)) {
      count++;
      lastDenominator++;
      if (remainder.getDenominator() % lastDenominator == 0) {
        Rational floor = remainder.floor(Rational.valueOf(1, lastDenominator));
        remainder = remainder.minus(floor);

        /*
         * Add the splits unless it takes us over the max number of egyptian fractions allowed
         */
        count = count + (int) floor.getNumerator() - 1;
        if (count < MAX_EGYPTIAN_FRACTIONS) {
          for (int i = 1; i <= floor.getNumerator(); i++) {
            Rational unitOfFloor = Rational.valueOf(1, floor.getDenominator());
            addEgyptianFractionPart(egyptianFractions, unitOfFloor, isNegative);
          }
        }
      }
    }
    if (count >= MAX_EGYPTIAN_FRACTIONS) {
      throw new OdinRuntimeException(
          "Overflow of " + count
              + " when creating egyptian fractions for " + this + ".  Remainder = " + remainder);
    }
    if (!remainder.equals(Wholes.ZERO)) {
      throw new OdinRuntimeException("Remainder, " + remainder.getDenominator()
          + ", from egyptian fraction of "
          + this + " is not zero");
    }
    return egyptianFractions.stream();
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
