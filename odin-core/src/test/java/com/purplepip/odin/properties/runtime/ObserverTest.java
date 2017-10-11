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

package com.purplepip.odin.properties.runtime;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.properties.runtime.ObservableProperty;
import com.purplepip.odin.properties.runtime.Observer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class ObserverTest {
  @Test
  public void testChange() {
    final AtomicInteger total = new AtomicInteger(0);
    final ObservableProperty<Integer> property = new ObservableProperty<>(1);
    Observer observer = () -> total.addAndGet(property.get());
    property.addObserver(observer);
    property.set(2);
    assertEquals(2, total.get());
    property.set(3);
    assertEquals(5, total.get());
    property.removeObserver(observer);
    property.set(1);
    assertEquals(5, total.get());
  }
}
