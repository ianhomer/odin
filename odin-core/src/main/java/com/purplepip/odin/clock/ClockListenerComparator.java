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

package com.purplepip.odin.clock;

import com.purplepip.odin.common.ListenerPriority;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Clock listener comparator.  It is important that some clock listeners get executed first,
 * for example runtime sequences need to have started before processors start.  This comparator
 * looks for the ListenerPriority annotation and sorts priorities with lower numbers first.
 */
public class ClockListenerComparator implements Comparator<PerformanceListener>, Serializable {
  private static final long serialVersionUID = 1;

  @Override
  public int compare(PerformanceListener listener1, PerformanceListener listener2) {
    int result = getPriority(listener1) - getPriority(listener2);
    if (result == 0) {
      /*
       * If priority is the same then order on the hash code.
       */
      return listener1.hashCode() - listener2.hashCode();
    }
    return result;
  }

  private static int getPriority(PerformanceListener listener) {
    ListenerPriority annotation =
        listener.getClass().getAnnotation(ListenerPriority.class);
    return annotation == null ? 0 : annotation.value();
  }
}
