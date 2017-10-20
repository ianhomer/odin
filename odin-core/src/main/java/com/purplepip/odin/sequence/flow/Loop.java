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

package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.math.Bound;
import com.purplepip.odin.math.LessThan;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Tock used by flow to find next event.
 */
@Slf4j
@ToString
public class Loop {
  private Real length;
  private Bound position;
  private boolean isPositionReal;

  /**
   * Create a loop.
   *
   * @param loopLength loop length
   * @param position position in the loop
   */
  public Loop(Real loopLength, Bound position) {
    this.length = loopLength;
    this.position = position;
    isPositionReal = position instanceof Real;
    LOG.debug("Created loop ; position = {}, length = {}", position, loopLength);
  }

  /**
   * Get tock position for the start of this loop.
   *
   * @return start
   */
  public Real getStart() {
    return position.getLimit().floor(length);
  }

  /**
   * Get tock position relative to the start of the loop.
   *
   * @return tock position in loop
   */
  public Bound getPosition() {
    if (length.isNegative()) {
      /*
       * No looping.
       */
      return position;
    } else {
      if (isPositionReal) {
        return position.getLimit().modulo(length);
      }
      return LessThan.lessThan(position.getLimit().modulo(length));
    }
  }

  /**
   * Increment to the next less than boundary.
   */
  public void increment() {
    isPositionReal = false;
    position = LessThan.lessThan(position.getLimit().wholeFloor().plus(Wholes.ONE));
  }
}
