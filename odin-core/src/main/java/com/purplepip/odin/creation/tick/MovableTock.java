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

package com.purplepip.odin.creation.tick;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import lombok.ToString;

/**
 * Tock where the count can be moved.
 */
@ToString
public class MovableTock implements Tock {
  private Real position;
  private Tick tick;

  public MovableTock(Tock tock) {
    this.tick = tock.getTick();
    this.position = tock.getPosition();
  }

  public MovableTock(Tick tick, Real position) {
    this.tick = tick;
    this.position = position;
  }

  public void setPosition(Real position) {
    this.position = position;
  }

  public void increment() {
    setPosition(position.wholeFloor().plus(Wholes.ONE));
  }

  public void increment(Real increment) {
    setPosition(position.plus(increment));
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  @Override
  public Real getPosition() {
    return position;
  }
}
