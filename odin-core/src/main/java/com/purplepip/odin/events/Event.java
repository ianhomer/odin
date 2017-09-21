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

/**
 * Generic event describing something at a given time.  Time is relative to an origin with some
 * time units that are known from the context of use.
 */
public interface Event<A> {
  A getValue();

  /**
   * Get relative time.  The unit of time is dependent on implementation, e.g. it could be beats,
   * bars, seconds, or milliseconds.
   *
   * @return time
   */
  Real getTime();
}
