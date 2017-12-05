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
import com.purplepip.odin.operation.ChannelOperation;

/**
 * Strict ordered event comparator.  This allows operations on the queue to be place in a
 * predictable order.  It is required for tests where strict order makes assertions easier to
 * make, but is unnecessary for general runtime.
 */
public class StrictOperationEventComparator extends OperationEventComparator {
  @Override
  public int compare(OperationEvent x, OperationEvent y) {
    int result = super.compare(x, y);
    if (result == 0) {
      if (x.getOperation() instanceof ChannelOperation
          && y.getOperation() instanceof ChannelOperation) {
        return CompareHelper.compare(
            ((ChannelOperation) x.getOperation()).getChannel(),
            ((ChannelOperation) y.getOperation()).getChannel());
      }
      return 0;
    }
    return result;
  }
}
