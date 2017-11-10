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

package com.purplepip.odin.creation.triggers;

import static com.purplepip.odin.configuration.TriggerFactories.newTriggerFactory;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinRuntimeException;
import org.junit.Test;

public class TriggerFactoryTest {
  @Test(expected = OdinRuntimeException.class)
  public void testCreateTriggerWithNoRule() throws Exception {
    newTriggerFactory().newInstance(new GenericTrigger());
  }

  @Test
  public void testCreateTrigger() throws Exception {
    MutableTriggerConfiguration triggerConfiguration = new GenericTrigger();
    triggerConfiguration.setType("note");
    triggerConfiguration.setProperty("note.number", 60);
    triggerConfiguration.setProperty("note.velocity", 10);
    triggerConfiguration.setProperty("note.duration", 10);
    NoteTrigger trigger = newTriggerFactory().newInstance(triggerConfiguration, NoteTrigger.class);
    assertEquals(60, trigger.getNote().getNumber());
  }
}