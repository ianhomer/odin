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

package com.purplepip.odin.events;

import com.purplepip.odin.math.Real;
import lombok.ToString;

/**
 * An event whose value has been swallowed and as such the event should be ignored.
 */
@ToString
public class SwallowedEvent implements NullValueEvent {
  private Real time;

  public SwallowedEvent(Real time) {
    this.time = time;
  }

  @Override
  public Object getValue() {
    return null;
  }

  @Override
  public Real getTime() {
    return time;
  }
}
