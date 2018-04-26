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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class GenericActionTest {
  @Test
  public void testCopy() {
    assertCopy(new GenericAction("start").name("test"));
    assertCopy(new GenericAction().name("test"));
    assertCopy(new GenericAction("test", 1).name("test"));
    assertCopy(new GenericAction().property("property1","value1"));
  }

  private void assertCopy(GenericAction action) {
    assertNotNull(action.toString());
    ActionConfiguration copy = action.copy();
    assertEquals(action, copy);
    assertEquals(action.hashCode(), copy.hashCode());
    GenericAction notEqualsAction = new GenericAction("test", 1).name("test")
        .property("property1", "not-equals-value");
    assertNotEquals(action, notEqualsAction);
    assertNotEquals(action.hashCode(), notEqualsAction.hashCode());
  }

  @Test
  public void testToString() {
    assertEquals("GenericAction(type=start, name=test, properties=[property1=value1])",
        new GenericAction("start").name("test")
            .property("property1", "value1").toString());
  }

  @Test
  public void testGenericAction() {
    GenericAction action = new GenericAction("start").name("test");
    assertEquals("start", action.copy().getType());
  }
}