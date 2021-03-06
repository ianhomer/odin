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

package com.purplepip.odin.configuration;

import static com.purplepip.odin.configuration.ActionFactories.newActionFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.action.ActionConfiguration;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.action.GenericAction;
import com.purplepip.odin.creation.action.StartAction;
import org.junit.jupiter.api.Test;

public class ActionFactoriesTest {
  @Test
  public void testCreateFromSpecialised() {
    ActionFactory factory = newActionFactory();
    ActionConfiguration configuration = new StartAction();
    Action action = factory.newInstance(configuration);
    assertEquals("start", action.getType());
    assertTrue(action instanceof StartAction);
  }

  @Test
  public void testCreateFromGeneric() {
    ActionFactory factory = newActionFactory();
    ActionConfiguration configuration = new GenericAction("start");
    Action action = factory.newInstance(configuration);
    assertEquals("start", action.getType());
    assertTrue(action instanceof StartAction);
  }

}