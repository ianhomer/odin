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

package com.purplepip.odin.clock;

import com.purplepip.odin.math.Real;

public abstract class AbstractClock implements Clock {
  private Real maxLookForward;

  protected void setMaxLookForward(Real maxLookForward) {
    this.maxLookForward = maxLookForward;
  }

  @Override
  public final Real getDuration(long microseconds) {
    return getDurationFromMicroseconds(microseconds, getMicroseconds());
  }

  @Override
  public final Real getDuration(long microseconds, Real count) {
    return getDurationFromMicroseconds(microseconds, getMicroseconds(count));
  }

  private Real getDurationFromMicroseconds(long microsecondsDuration, long microsecondsPosition) {
    return getPosition(microsecondsPosition + microsecondsDuration)
        .minus(getPosition(microsecondsPosition));
  }

  @Override
  public Real getMaxLookForward() {
    return maxLookForward;
  }
}
