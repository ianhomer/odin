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

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.layer.DefaultLayer;
import org.junit.jupiter.api.Test;

class UnmodifiableConductorTest {
  @Test
  void testGetActive() {
    MutableThings<Conductor> mutableConductors = new MutableThings<>();
    DefaultLayer layer = new DefaultLayer().name("test").tick(Ticks.BEAT);
    layer.setTick(Ticks.BEAT);
    LayerConductor newConductor = new LayerConductor(layer, newPrecisionBeatClock(60));
    mutableConductors.add(newConductor);
    Things<Conductor> conductors = new UnmodifiableConductors(mutableConductors);
    Conductor conductor = conductors.stream().findFirst().orElse(null);
    assertNotNull(conductor);
    assertTrue(conductor instanceof UnmodifiableConductor);
    assertTrue(conductor.isActive(1));
  }
}