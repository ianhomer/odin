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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.sequence.layer.DefaultLayer;
import org.junit.Test;

public class UnmodifiableConductorTest {
  @Test
  public void getActive() throws Exception {
    MutableThings<Conductor> mutableConductors = new MutableThings<>();
    mutableConductors.add(new DefaultConductor(new DefaultLayer("test")));
    Things<Conductor> conductors = new UnmodifiableConductors(mutableConductors);
    Conductor conductor = conductors.stream().findFirst().orElse(null);
    assertNotNull(conductor);
    assertTrue(conductor instanceof UnmodifiableConductor);
    assertTrue(conductor.getActive());
  }
}