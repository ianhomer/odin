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

package com.purplepip.odin.devices;

import static com.purplepip.odin.devices.NamedHandle.asHandleList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class HandleComparatorTest {

  @Test
  void shouldCompareOk() {
    List<Handle> handles = asHandleList("A", "C", "B", "F", "E");
    HandleComparator comparator = new HandleComparator(handles);
    assertTrue(comparator.compare(new NamedHandle("A"), new NamedHandle("B")) < 0);
    assertTrue(comparator.compare(new NamedHandle("B"), new NamedHandle("C")) > 0);
    assertEquals(0, comparator.compare(new NamedHandle("A"), new NamedHandle("A")));
    assertTrue(comparator.compare(new NamedHandle("A"), new NamedHandle("a")) < 0);
    assertTrue(comparator.compare(new NamedHandle("G"), new NamedHandle("H")) < 0);
    assertTrue(comparator.compare(new NamedHandle("I"), new NamedHandle("H")) > 0);
  }
}