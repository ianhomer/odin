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

package com.purplepip.odin.sequence.conductor;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.tick.Ticks;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LayerConductor implements Conductor {
  private Layer layer;
  private BeatClock clock;
  private TickConverter tickConverter;

  public LayerConductor(BeatClock clock) {
    this.clock = clock;
  }

  @Override
  public long getId() {
    return layer.getId();
  }

  @Override
  public String getName() {
    return layer.getName();
  }

  public Layer getLayer() {
    return layer;
  }

  /**
   * Set layer for the layer conductor.
   *
   * @param layer layer
   */
  public void setLayer(Layer layer) {
    this.layer = layer;
    tickConverter = new DefaultTickConverter(clock,
        () -> Ticks.MICROSECOND, layer::getTick, layer::getOffset);

  }

  @Override
  public boolean isActive(long microseconds) {
    Real tock = tickConverter.convert(Whole.valueOf(microseconds));
    boolean result = tock.gt(Wholes.ZERO)
        && (layer.getLength() <= 0 || tock.lt(Whole.valueOf(layer.getLength())));
    LOG.debug("isActive : {}, {}, {}, {}", result, tock, microseconds, layer.getLength());
    return result;
  }
}
