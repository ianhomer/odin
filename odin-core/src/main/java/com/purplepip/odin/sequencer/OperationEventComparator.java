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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.CompareHelper;
import java.io.Serializable;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;

/**
 * Comparator for a operation message.
 */
@Slf4j
public class OperationEventComparator implements Comparator<OperationEvent>, Serializable {
  private static final long serialVersionUID = 1;

  /**
   * Compare two operation events.
   *
   * @param x first operation
   * @param y second operation
   * @return -1, 0 or 1 depending on relative positioning of the two operation events
   */
  @Override
  public int compare(OperationEvent x, OperationEvent y) {
    return CompareHelper.compare(x.getTime(), y.getTime());
  }
}
