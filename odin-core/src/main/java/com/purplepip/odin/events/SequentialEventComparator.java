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

package com.purplepip.odin.events;

import java.io.Serializable;
import java.util.Comparator;

public class SequentialEventComparator implements Comparator<Event>, Serializable {
  private static final long serialVersionUID = 1;

  @Override
  public int compare(Event event1, Event event2) {
    return (int) event1.getTime().minus(event2.getTime()).floor();
  }
}
