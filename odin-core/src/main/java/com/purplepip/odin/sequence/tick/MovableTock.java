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

package com.purplepip.odin.sequence.tick;

import com.purplepip.odin.math.Rational;
import lombok.ToString;

/**
 * Tock where the count can be moved.
 */
@ToString
public class MovableTock implements Tock {
  /*
   * TODO : Rename count to position to improve readability
   */
  private Rational count;
  private Tick tick;

  public MovableTock(Tock tock) {
    this.tick = tock.getTick();
    this.count = tock.getCount();
  }

  public MovableTock(Tick tick, Rational count) {
    this.tick = tick;
    this.count = count;
  }

  public void setCount(Rational count) {
    this.count = count;
  }

  public void increment() {
    count = count.add(new Rational(1));
  }

  public void increment(Rational increment) {
    count = count.add(increment);
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  @Override
  public Rational getCount() {
    return count;
  }
}
