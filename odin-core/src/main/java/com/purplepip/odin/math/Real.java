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

public interface Real extends Bound {
  double getValue();

  Real times(Real real);

  Real divide(Real real);

  long floor();

  Real floor(Real radix);

  Whole wholeFloor();

  Rational toRational();

  boolean le(Real real);

  Real modulo(Real real);

  Real plus(Real real);

  /**
   * Add rational number to this rational number.
   *
   * @param rational rational number to add
   * @return result of addition
   */
  Real plus(Rational rational);

  Real plus(Whole whole);

  Real minus(Real real);

  Real minus(Rational rational);

  Real minus(Whole whole);

  Whole wholeCeiling();

  boolean isPositive();

  boolean isNegative();

  boolean gt(Real real);

  boolean ge(Real real);

  long nextFloor();

  Whole nextWholeFloor();

  long ceiling();

  Real negative();

  Real absolute();
}
