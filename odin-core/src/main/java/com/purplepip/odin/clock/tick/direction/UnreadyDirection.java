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

package com.purplepip.odin.clock.tick.direction;

import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Real;

/**
 * Source tick and target tick in a tick converter can be created lazily and get updated
 * based on source and target tick changes.  If at any time one of the ticks is not set then
 * the direction is not ready to be used and is waiting for an update.
 */
public class UnreadyDirection extends AbstractDirection {
  /**
   * Create a new direction that is not ready for use.
   *
   * @param sourceTick source tick
   * @param targetTick target tick
   */
  public UnreadyDirection(Tick sourceTick, Tick targetTick) {
    super(sourceTick, targetTick);
    if (sourceTick != null && targetTick != null) {
      throw new OdinRuntimeException("UnreadyDirection should only be created if either"
          + " source tick or target tick is null");
    }
  }

  @Override
  public Real scaleTime(Real time) {
    if (getSourceTick() == null) {
      throw new OdinRuntimeException("Direction is not ready, source tick is null");
    } else {
      throw new OdinRuntimeException("Direction is not ready, target tick is null");
    }
  }
}
