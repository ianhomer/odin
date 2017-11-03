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

import com.purplepip.odin.properties.thing.AbstractPropertiesThing;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractTimeThing extends AbstractPropertiesThing
    implements MutableTimeThing {
  private boolean enabled = false;
  private Tick tick;
  /**
   * Set the length of the thing in ticks.
   */
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
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  protected AbstractTimeThing copy(AbstractTimeThing copy, AbstractTimeThing original) {
    copy.enabled = original.enabled;
    copy.tick = original.tick;
    copy.length = original.length;
    copy.offset = original.offset;
    super.copy(copy, original);
    return copy;
  }

}
