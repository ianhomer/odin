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

package com.purplepip.odin.creation.action;

import static com.purplepip.odin.configuration.ActionFactories.newActionFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import org.junit.Test;

public class ListActionTest {
  @Test
  public void testMapGenericToPlugin() {
    Map<String, ActionConfiguration> map =
        ListAction.asActionConfigurationMap("test", new StartAction(), new EnableAction());
    assertEquals(3, map.size());
    map.values().forEach(value -> assertTrue(value instanceof GenericAction));
    ListAction action = ListAction.asListAction("test", map, newActionFactory());
    assertEquals(2, action.getRipples().count());
    assertTrue(action.getRipples().toArray()[0] instanceof StartAction);
    assertTrue(action.getRipples().toArray()[1] instanceof EnableAction);
  }
}