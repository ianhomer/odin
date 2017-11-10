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

package com.purplepip.odin.creation.flow;

public class DefaultFlowConfiguration implements FlowConfiguration {
  private static final int DEFAULT_MAX_FORWARD_SCAN = 10_000_000;

  private long maxForwardScan = DEFAULT_MAX_FORWARD_SCAN;

  /**
   * Max time in microseconds that a flow can scan forward to find the next event.
   *
   * @return max forward scan in microseconds
   */
  @Override
  public long getMaxForwardScan() {
    return maxForwardScan;
  }

  public DefaultFlowConfiguration setMaxForwardScan(int maxForwardScan) {
    this.maxForwardScan = maxForwardScan;
    return this;
  }
}
