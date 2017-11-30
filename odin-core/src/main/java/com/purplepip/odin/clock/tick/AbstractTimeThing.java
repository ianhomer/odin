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

package com.purplepip.odin.clock.tick;

import com.purplepip.odin.common.Stringy;
import com.purplepip.odin.properties.thing.AbstractPropertiesThing;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractTimeThing extends AbstractPropertiesThing
    implements MutableTimeThing {
  private boolean enabled = true;
  private Tick tick = Ticks.BEAT;
  /*
   * Length of the thing in ticks.
   */
  private long length = -1;
  private long offset;

  public AbstractTimeThing() {
    super();
  }

  public AbstractTimeThing(long id) {
    super(id);
  }

  protected AbstractTimeThing copy(AbstractTimeThing copy) {
    copy.enabled = this.enabled;
    copy.tick = this.tick;
    copy.length = this.length;
    copy.offset = this.offset;
    super.copy(copy);
    return copy;
  }

  public AbstractTimeThing enabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public AbstractTimeThing length(long length) {
    this.length = length;
    return this;
  }

  public AbstractTimeThing tick(Tick tick) {
    this.tick = tick;
    return this;
  }

  public AbstractTimeThing offset(long offset) {
    this.offset = offset;
    return this;
  }

  public String toString() {
    return Stringy.of(AbstractTimeThing.class, this)
        .add("name", getName())
        .add("properties", getPropertyEntries())
        .add("enabled", enabled)
        .add("tick", tick)
        .add("length", length)
        .add("offset", offset)
        .build();
  }
}
