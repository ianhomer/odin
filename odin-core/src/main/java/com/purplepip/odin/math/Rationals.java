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
import lombok.extern.slf4j.Slf4j;

/**
 * Rational numbers.
 */
@Slf4j
public final class Rationals {
  public static final Rational HALF = valueOf(1, 2);
  public static final Rational THIRD = valueOf(1, 3);
  public static final Rational TWO_THIRDS = valueOf(2, 3);
  public static final Rational FOUR_THIRDS = valueOf(4, 3);
  public static final Rational QUARTER = valueOf(1, 4);
  public static final Rational THREE_QUARTERS = valueOf(3, 4);
  public static final Rational EIGTH = valueOf(1, 8);

  private Rationals() {
  }

  /**
   * Create Whole number.  Please use the Whole.valueOf method.  This only exists
   * to prevent auto use of the valueOf with a double argument when an long is passed in.
   *
   * @param integer integer to create the whole number from
   * @return whole number
   */
  public static Whole valueOf(long integer) {
    StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
    LOG.warn("Please call 'Whole.valueOf({})' not 'Rational.valueOf({})' "
            + "in {} @ line {} "
            + "given you know at compile time you what a whole number", integer, integer,
        stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
    return Wholes.valueOf(integer);
  }

  /**
   * Create a rational number from a given numerator and denominator.  Note a Whole number is
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
   * Create a rational number from a given numerator and denominator.  Note a Whole number is
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
      return new Whole(numerator);
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
        return valueOf(Long.parseLong(firstPart),
            Long.parseLong(secondPart));
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
}
