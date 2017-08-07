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

public class Whole extends Rational {
  public Whole(long numerator) {
    super(numerator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real plus(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return Real.valueOf(getNumerator() + whole.getNumerator());
    }
    return super.plus(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real minus(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return Real.valueOf(getNumerator() - whole.getNumerator());
    }
    return super.minus(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real times(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return Real.valueOf(getNumerator() * whole.getNumerator());
    }
    return super.times(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real divide(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return Real.valueOf(getNumerator() / whole.getNumerator());
    }
    return super.divide(real);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Real modulo(Real real) {
    if (real instanceof Whole) {
      Whole whole = (Whole) real;
      return Real.valueOf(getNumerator() % whole.getNumerator());
    }
    return super.modulo(real);
  }
}
