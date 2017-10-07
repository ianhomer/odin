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

package com.purplepip.odin.sequence.tick;

import com.purplepip.odin.bag.AbstractThing;

public abstract class AbstractTimeThing extends AbstractThing implements MutableTimeThing {
  private boolean active = false;
  private Tick tick;
  private long length = -1;
  private long offset;

  public AbstractTimeThing() {
    super();
  }

  public AbstractTimeThing(String name) {
    super(name);
  }

  public AbstractTimeThing(long id) {
    super(id);
  }

  @Override
  public void setTick(Tick tick) {
    this.tick = tick;
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  /**
   * Set the length of the thing in ticks.
   *
   * @param length length of thing in ticks
   */
  @Override
  public void setLength(long length) {
    this.length = length;
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public void setOffset(long offset) {
    this.offset = offset;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public boolean isActive() {
    return active;
  }
}
