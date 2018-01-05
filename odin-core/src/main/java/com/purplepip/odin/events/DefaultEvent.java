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
import lombok.extern.slf4j.Slf4j;

/**
 * Default Event.
 */
@Slf4j
public class DefaultEvent extends AbstractEvent<Object> {
  /**
   * Create a default event.
   *
   * @param value value for the event
   * @param time time of the event
   */
  public DefaultEvent(Object value, long time) {
    super(value, time);
  }

  /**
   * Create a default event.
   *
   * @param value value for the event
   * @param time time of the event
   */
  public DefaultEvent(Object value, Real time) {
    super(value, time);
  }
}
