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

package com.purplepip.odin.creation.conductor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.layer.DefaultLayer;
import org.junit.Test;

public class LayerRuntimeTest {
  @Test
  public void testLayerRuntime() {
    DefaultLayer layer = new DefaultLayer("test");
    layer.setTick(Ticks.BEAT);
    LayerConductor conductor = new LayerConductor(mock(BeatClock.class));
    conductor.setLayer(layer);
    assertEquals(layer, conductor.getLayer());
  }
}