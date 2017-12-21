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

abstract class AbstractRational extends AbstractReal implements Rational {
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
