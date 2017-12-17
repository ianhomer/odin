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

/**
 * Rational numbers.
 */
public final class Rationals {
  public static final Rational MINUS_ONE = Wholes.MINUS_ONE;
  public static final Rational ZERO = Wholes.ZERO;
  public static final Rational ONE = Wholes.ONE;
  public static final Rational TWO = Wholes.TWO;
  public static final Rational HALF = Rational.valueOf(1, 2);
  public static final Rational THIRD = Rational.valueOf(1, 3);
  public static final Rational TWO_THIRDS = Rational.valueOf(2, 3);
  public static final Rational FOUR_THIRDS = Rational.valueOf(4, 3);
  public static final Rational QUARTER = Rational.valueOf(1, 4);
  public static final Rational THREE_QUARTERS = Rational.valueOf(3, 4);
  public static final Rational EIGTH = Rational.valueOf(1, 8);

  private Rationals() {
  }
}
