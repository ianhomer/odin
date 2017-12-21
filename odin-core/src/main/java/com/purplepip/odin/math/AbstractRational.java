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
import com.purplepip.odin.common.OdinRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

abstract class AbstractRational extends AbstractReal implements Rational {
  private static final int MAX_EGYPTIAN_FRACTIONS = 20;

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

  @Override
  public Rational times(Rational rational) {
    return Rationals.valueOf(getNumerator() * rational.getNumerator(),
        getDenominator() * rational.getDenominator(), isSimplified());
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



  @Override
  public Rational divide(Rational rational) {
    return Rationals.valueOf(getNumerator() * rational.getDenominator(),
        getDenominator() * rational.getNumerator(), isSimplified());
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
  @Override
  public Rational modulo(Rational rational) {
    return Rationals.valueOf((getNumerator() * rational.getDenominator())
            % (rational.getNumerator() * getDenominator()),
        rational.getDenominator() * getDenominator(), isSimplified());
  }

  /**
   * Calculate floor from a value we know is rational.
   *
   * @param rational rational number
   * @return floored value
   */
  @Override
  public Rational floor(Rational rational) {
    return Rationals.valueOf(
        floorNumerator(getNumerator(), getDenominator(),
            rational.getNumerator(), rational.getDenominator()),
        floorDenominator(getDenominator(), rational.getDenominator()),
        isSimplified());
  }


  /**
   * Get egyptian fractions with integer part split into multiple ones.
   *
   * @return egyptian fractions
   */
  @JsonIgnore
  @Override
  public Stream<Rational> getEgyptianFractions() {
    return getEgyptianFractions(1);
  }

  /**
   * Get egyptian fractions.
   *
   * @param maxIntegerPart Max integer part
   * @return egyptian fractions
   */
  @Override
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
        Rational floor = remainder.floor(Rationals.valueOf(1, lastDenominator, isSimplified()));
        remainder = remainder.minus(floor);

        /*
         * Add the splits unless it takes us over the max number of egyptian fractions allowed
         */
        count = count + (int) floor.getNumerator() - 1;
        for (int i = 1; i <= floor.getNumerator() && count <= MAX_EGYPTIAN_FRACTIONS; i++) {
          Rational unitOfFloor = Rationals.valueOf(1, floor.getDenominator(), isSimplified());
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

  private static void addEgyptianFractionPart(List<Rational> egyptianFractions,
                                              Rational part, boolean isNegative) {
    if (isNegative) {
      egyptianFractions.add(part.negative());
    } else {
      egyptianFractions.add(part);
    }
  }
}
