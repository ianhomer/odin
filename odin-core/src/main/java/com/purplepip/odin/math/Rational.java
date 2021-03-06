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

import java.util.stream.Stream;

public interface Rational extends Real {
  long getNumerator();

  long getDenominator();

  Rational plus(Rational rational);

  Rational plus(Whole whole);

  Rational minus(Rational rational);

  Rational minus(Whole whole);

  Rational times(Rational rational);

  Rational divide(Rational rational);

  Rational modulo(Rational rational);

  Rational negative();

  Rational absolute();

  Rational getLimit();

  boolean isSimplified();

  /**
   * Get egyptian fractions with integer part split into multiple ones.
   *
   * @return egyptian fractions
   */
  Stream<Rational> getEgyptianFractions();

  /**
   * Get egyptian fractions.
   *
   * @param maxIntegerPart Max integer part
   * @return egyptian fractions
   */
  Stream<Rational> getEgyptianFractions(int maxIntegerPart);

  Rational asMutable();

  Rational asImmutable();
}
