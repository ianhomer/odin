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

import com.purplepip.odin.common.OdinRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/** Rational numbers. */
@Slf4j
public final class Rationals {
  private static final int MAX_EGYPTIAN_FRACTIONS = 20;

  public static final Rational HALF = valueOf(1, 2);
  public static final Rational THIRD = valueOf(1, 3);
  public static final Rational TWO_THIRDS = valueOf(2, 3);
  public static final Rational FOUR_THIRDS = valueOf(4, 3);
  public static final Rational QUARTER = valueOf(1, 4);
  public static final Rational THREE_QUARTERS = valueOf(3, 4);
  public static final Rational EIGTH = valueOf(1, 8);

  private Rationals() {}

  /**
   * Create Whole number. Please use the Whole.valueOf method. This only exists to prevent auto use
   * of the valueOf with a double argument when an long is passed in.
   *
   * @param integer integer to create the whole number from
   * @return whole number
   */
  public static Whole valueOf(long integer) {
    StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
    LOG.warn(
        "Please call 'Whole.valueOf({})' not 'Rational.valueOf({})' "
            + "in {} @ line {} "
            + "given you know at compile time you what a whole number",
        integer,
        integer,
        stackTraceElement.getClassName(),
        stackTraceElement.getLineNumber());
    return Wholes.valueOf(integer);
  }

  /**
   * Create a rational number from a given numerator and denominator. Note a Whole number is
   * returned if the numerator is multiple of the denominator.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @return rational number
   */
  public static Rational valueOf(long numerator, long denominator) {
    return valueOf(numerator, denominator, true);
  }

  /**
   * Create a rational number from a given numerator and denominator. Note a Whole number is
   * returned if the numerator is multiple of the denominator.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @param simplify whether to simplify the rational
   * @return rational number
   */
  public static Rational valueOf(long numerator, long denominator, boolean simplify) {
    if (denominator == 0) {
      throw new OdinRuntimeException("A rational number MUST has a non-zero denominator");
    }
    if (denominator < 0) {
      return valueOf(-numerator, -denominator, simplify);
    }
    if (numerator == 0 || denominator == 1) {
      return Wholes.valueOf(numerator);
    }
    if (simplify) {
      return simplified(numerator, denominator);
    }
    return new ConcreteRational(numerator, denominator, false);
  }

  /**
   * Create rational number from a string.
   *
   * @param value rational value as a string
   * @return rational number
   */
  public static Rational valueOf(String value) {
    if (value == null || value.length() == 0) {
      return Wholes.ZERO;
    }
    int indexOfSeparator = value.indexOf('/');
    try {
      if (indexOfSeparator > -1) {
        String firstPart = value.substring(0, indexOfSeparator).trim();
        String secondPart = value.substring(indexOfSeparator + 1).trim();
        return valueOf(Long.parseLong(firstPart), Long.parseLong(secondPart));
      }
      return Wholes.valueOf(Long.parseLong(value));
    } catch (NumberFormatException e) {
      throw new OdinRuntimeException("Cannot get value of " + value, e);
    }
  }

  private static Rational simplified(long numerator, long denominator) {
    // TODO : This is an expensive operation for large denominators, flow should be using double
    // once converted to seconds
    long newNumerator = numerator;
    long newDenominator = denominator;
    /*
     * Reduce two negatives to positives.
     */
    if (newDenominator < 0) {
      newNumerator = -newNumerator;
      newDenominator = -newDenominator;
    }

    if (numerator % denominator == 0) {
      return Wholes.valueOf(numerator / denominator);
    }

    for (long i = 2; i <= newDenominator / i; i++) {
      while (newDenominator % i == 0 && newNumerator % i == 0) {
        newDenominator /= i;
        newNumerator /= i;
      }
    }
    if (newDenominator == 1) {
      return Wholes.valueOf(newNumerator);
    }
    return new ConcreteRational(newNumerator, newDenominator, true);
  }

  static long floorNumerator(
      long numerator1, long denominator1, long numerator2, long denominator2) {
    long product1 = numerator1 * denominator2;
    long product2 =  numerator2 * denominator1;
    return product1 - (product1 % product2);
  }

  static long floorDenominator(long denominator1, long denominator2) {
    return denominator1 * denominator2;
  }

  static Stream<Rational> getEgyptianFractions(Rational value, int maxIntegerPart) {
    List<Rational> egyptianFractions = new ArrayList<>();
    Rational remainder = value;
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
        Rational floor =
            remainder.floor(Rationals.valueOf(1, lastDenominator, value.isSimplified()));
        remainder = remainder.minus(floor);

        /*
         * Add the splits unless it takes us over the max number of egyptian fractions allowed
         */
        count = count + (int) floor.getNumerator() - 1;
        for (int i = 1; i <= floor.getNumerator() && count <= MAX_EGYPTIAN_FRACTIONS; i++) {
          Rational unitOfFloor = Rationals.valueOf(1, floor.getDenominator(), value.isSimplified());
          addEgyptianFractionPart(egyptianFractions, unitOfFloor, isNegative);
        }
      }
    }
    if (count > MAX_EGYPTIAN_FRACTIONS) {
      throw new OdinRuntimeException(
          "Overflow of "
              + count
              + " when creating egyptian fractions for "
              + value
              + ".  Remainder = "
              + remainder);
    }
    if (!remainder.equals(Wholes.ZERO)) {
      throw new OdinRuntimeException(
          "Remainder, "
              + remainder.getDenominator()
              + ", from egyptian fraction of "
              + value
              + " is not zero");
    }
    return egyptianFractions.stream();
  }

  static void addEgyptianFractionPart(
      List<Rational> egyptianFractions, Rational part, boolean isNegative) {
    if (isNegative) {
      egyptianFractions.add(part.negative());
    } else {
      egyptianFractions.add(part);
    }
  }
}
