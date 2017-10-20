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

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A "less than" is a number that is just less than the given number.
 */
@ToString
@EqualsAndHashCode
public class LessThan implements Bound {
  private Real limit;

  public static LessThan lessThan(Real limit) {
    return new LessThan(limit);
  }

  private LessThan(Real limit) {
    this.limit = limit;
  }

  /**
   * A "less than" number is always less than the given limit even for equality, so we simple map
   * less than function to less than or equals of the limit.
   *
   * @param real real number to compare
   * @return true if this less than number is less than the given number.
   */
  @Override
  public boolean lt(Real real) {
    return limit.le(real);
  }

  public Real getLimit() {
    return limit;
  }
}
