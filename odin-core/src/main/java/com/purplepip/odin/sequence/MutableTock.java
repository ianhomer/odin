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

package com.purplepip.odin.sequence;

import lombok.ToString;

/**
 * Mutable tock.
 */
@ToString
public class MutableTock implements Tock {
  private long count;
  private Tick tick;

  public MutableTock(Tock tock) {
    this.tick = tock.getTick();
    this.count = tock.getCount();
  }

  public MutableTock(Tick tick, long count) {
    this.tick = tick;
    this.count = count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public void increment() {
    count++;
  }

  public void increment(long increment) {
    count = count + increment;
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  @Override
  public long getCount() {
    return count;
  }
}
