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

public class ConcreteWhole extends AbstractWhole {
  /*
   * Local property for numerator for direct access.
   */
  private final long value;

  public ConcreteWhole(long value) {
    this.value = value;
  }

  @Override
  public long getNumerator() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Whole plus(Whole whole) {
    return Wholes.valueOf(value + whole.getNumerator());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Whole minus(Whole whole) {
    return Wholes.valueOf(value - whole.getNumerator());
  }

  @Override
  public Whole times(Whole whole) {
    return Wholes.valueOf(getNumerator() * whole.getNumerator());
  }

  @Override
  public Rational divide(Whole whole) {
    return Rationals.valueOf(getNumerator(), whole.getNumerator());
  }

  @Override
  public Whole modulo(Whole whole) {
    return Wholes.valueOf(getNumerator() % whole.getNumerator());
  }

  @Override
  public Whole floor(Whole radix) {
    return Wholes.valueOf(getNumerator() - (getNumerator() % radix.getNumerator()));
  }

  @Override
  public Whole nextWholeFloor() {
    return Wholes.valueOf(nextFloor());
  }

  @Override
  public Whole negative() {
    return Wholes.valueOf(-getNumerator());
  }

  @Override
  public Whole absolute() {
    if (isNegative()) {
      return Wholes.valueOf(-getNumerator());
    }
    return this;
  }

  @Override
  public Whole asMutable() {
    return Wholes.mutableOf(value);
  }

  @Override
  public Whole asImmutable() {
    return this;
  }
}
