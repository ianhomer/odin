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
import com.fasterxml.jackson.annotation.JsonProperty;
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

  private final long numerator;
  private final long denominator;
  private final boolean simplified;
  private static final Map<Rational, Character> fractionCharacters = new HashMap<>();
  private double value;
  private boolean valueCalculated;

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
  private Rational(long numerator, long denominator) {
    this(numerator, denominator, false);
  }

  /**
   * Create rational number.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @param simplified whether rational was simplified
   */
  protected Rational(long numerator, long denominator, boolean simplified) {
    this.numerator = numerator;
    this.denominator = denominator;
    this.simplified = simplified;
  }

  public long getNumerator() {
    return numerator;
  }

  public long getDenominator() {
    return denominator;
  }

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
    return Rationals.valueOf(numerator * rational.denominator
            + rational.getNumerator() * getDenominator(),
        denominator * rational.denominator, simplified);
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
    return Rationals.valueOf(numerator * rational.getDenominator()
            - rational.getNumerator() * getDenominator(),
        denominator * rational.denominator, simplified);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real times(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return times(rational);
    }
    return super.times(real);
  }

  public Rational times(Rational rational) {
    return Rationals.valueOf(numerator * rational.getNumerator(),
        denominator * rational.getDenominator(), simplified);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real divide(Real real) {
    if (real instanceof Rational) {
      Rational rational = (Rational) real;
      return divide(rational);
    }
    return super.divide(real);
  }

  public Rational divide(Rational rational) {
    return Rationals.valueOf(numerator * rational.getDenominator(),
        denominator * rational.getNumerator(), simplified);
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
    return Rationals.valueOf((numerator * rational.getDenominator())
            % (rational.getNumerator() * denominator),
        rational.getDenominator() * denominator, simplified);
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
  @Override
  public Rational floor(Rational rational) {
    long product1 = numerator * rational.getDenominator();
    long product2 = rational.getNumerator() * denominator;
    long product3 = denominator * rational.getDenominator();
    return Rationals.valueOf(product1 - (product1 % product2), product3, simplified);
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

  private static void addEgyptianFractionPart(List<Rational> egyptianFractions,
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
  @JsonIgnore
  public Stream<Rational> getEgyptianFractions() {
    return getEgyptianFractions(1);
  }

  /**
   * Get egyptian fractions.
   *
   * @param maxIntegerPart Max integer part
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

    /*
     * Split the integer part into multiple integers less than or equal to the max integer.
     */
    Whole maxWholePart = Wholes.valueOf(maxIntegerPart);
    while (egyptianFractions.size() < MAX_EGYPTIAN_FRACTIONS && remainder.ge(maxWholePart)) {
      remainder = remainder.minus(maxWholePart);
      addEgyptianFractionPart(egyptianFractions, maxWholePart, isNegative);
    }

    /*
     * Split out the remaining integer less than the max integer part
     */
    if (remainder.gt(Wholes.ONE)) {
      Rational part = remainder.floor(Wholes.ONE);
      remainder = remainder.minus(part);
      addEgyptianFractionPart(egyptianFractions, part, isNegative);
    }

    /*
     * Now split the fractions
     */
    int lastDenominator = 0;

    int count = egyptianFractions.size();
    while (count <= MAX_EGYPTIAN_FRACTIONS && remainder.gt(Wholes.ZERO)) {
      count++;
      lastDenominator++;
      if (remainder.getDenominator() % lastDenominator == 0) {
        Rational floor = remainder.floor(Rationals.valueOf(1, lastDenominator, simplified));
        remainder = remainder.minus(floor);

        /*
         * Add the splits unless it takes us over the max number of egyptian fractions allowed
         */
        count = count + (int) floor.getNumerator() - 1;
        for (int i = 1; i <= floor.getNumerator() && count <= MAX_EGYPTIAN_FRACTIONS; i++) {
          Rational unitOfFloor = Rationals.valueOf(1, floor.getDenominator(), simplified);
          addEgyptianFractionPart(egyptianFractions, unitOfFloor, isNegative);
        }
      }
    }
    if (count > MAX_EGYPTIAN_FRACTIONS) {
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
