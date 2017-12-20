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

abstract class AbstractReal implements Real {
  /**
   * Minus a real number.
   *
   * @param real real number to minus
   * @return result of subtraction
   */
  @Override
  public Real minus(Real real) {
    return Reals.valueOf(getValue() - real.getValue());
  }

  /**
   * Multiply a real number.
   *
   * @param real real number to multiply
   * @return result of multiplication
   */
  @Override
  public Real times(Real real) {
    return Reals.valueOf(getValue() * real.getValue());
  }

  /**
   * Divide a real number.
   *
   * @param real real number to divide
   * @return result of division
   */
  @Override
  public Real divide(Real real) {
    return Reals.valueOf(getValue() / real.getValue());
  }

  /**
   * Calculate modulo.
   *
   * @param real real number
   * @return modulo result
   */
  @Override
  public Real modulo(Real real) {
    return Reals.valueOf(getValue() % real.getValue());
  }

  /**
   * Calculate the nearest multiple of base less than this real number.
   *
   * @param radix radix
   * @return floored value
   */
  @Override
  public Real floor(Real radix) {
    double flooredValue = getValue() - (getValue() % radix.getValue());
    if (radix instanceof Whole) {
      /*
       * Flooring with whole radix will always give a whole number, so lets be explicit about it.
       */
      return Wholes.valueOf((long) flooredValue);
    }
    return Reals.valueOf(flooredValue);
  }

  /**
   * Calculate the smallest whole number greater than this real number.  Note that this function is
   * NOT the standard "ceil" function since this function will return the next whole number for
   * a whole number.
   *
   * @return next ceiling.
   */
  @Override
  public long nextFloor() {
    return floor() + 1;
  }

  @Override
  public boolean ge(Real real) {
    return getValue() >= real.getValue();
  }

  @Override
  public boolean gt(Real real) {
    return getValue() > real.getValue();
  }

  @Override
  public boolean lt(Real real) {
    return getValue() < real.getValue();
  }

  @Override
  public boolean le(Real real) {
    return getValue() <= real.getValue();
  }

  /**
   * Calculate the largest whole number less than or equal to this real number.
   *
   * @return floored value
   */
  @Override
  public Whole wholeFloor() {
    return Wholes.valueOf(floor());
  }

  /**
   * Calculate the largest whole number greater than or equal to this real number.
   *
   * @return floored value
   */
  @Override
  public Whole wholeCeiling() {
    return Wholes.valueOf(ceiling());
  }

  /**
   * Calculate the smallest whole number greater than this real number.  Note that this function is
   * NOT the standard "ceil" function since this function will return the next whole number for
   * a whole number.
   *
   * @return next ceiling.
   */
  @Override
  public Whole nextWholeFloor() {
    return Wholes.valueOf(nextFloor());
  }
}
