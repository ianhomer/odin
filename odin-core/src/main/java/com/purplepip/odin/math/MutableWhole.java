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

public class MutableWhole extends AbstractWhole {
  private long value;

  MutableWhole(long value) {
    this.value = value;
  }

  @Override
  public long getNumerator() {
    return value;
  }

  @Override
  public Whole plus(Whole whole) {
    value += whole.getNumerator();
    return this;
  }

  @Override
  public Whole minus(Whole whole) {
    value -= whole.getNumerator();
    return this;
  }

  @Override
  public Whole times(Whole whole) {
    value *= whole.getNumerator();
    return this;
  }

  @Override
  public Rational divide(Whole whole) {
    if (value % whole.getNumerator() == 0) {
      value /= whole.getNumerator();
      return this;
    }
    return Rationals.valueOf(getNumerator(), whole.getNumerator());
  }

  @Override
  public Whole modulo(Whole whole) {
    value = value % whole.getNumerator();
    return this;
  }

  @Override
  public Whole floor(Whole radix) {
    value = value - (value % radix.getNumerator());
    return this;
  }

  @Override
  public Whole nextWholeFloor() {
    value = nextFloor();
    return this;
  }

  @Override
  public Whole negative() {
    value = -value;
    return this;
  }

  @Override
  public Whole absolute() {
    if (isNegative()) {
      value = -value;
    }
    return this;
  }

  @Override
  public Whole asMutable() {
    return this;
  }

  @Override
  public Whole asImmutable() {
    return Wholes.valueOf(value);
  }
}
