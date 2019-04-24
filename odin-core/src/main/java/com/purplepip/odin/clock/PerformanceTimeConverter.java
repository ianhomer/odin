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

/**
 * Time converter that converts from performance time to local time by a simple offset.
 */
public class PerformanceTimeConverter implements TimeConverter, PerformanceListener {
  private final MicrosecondPositionProvider microsecondPositionProvider;
  private long offset;

  public PerformanceTimeConverter(MicrosecondPositionProvider microsecondPositionProvider) {
    this.microsecondPositionProvider = microsecondPositionProvider;
  }

  public long convert(long performanceTime) {
    return performanceTime + offset;
  }

  @Override
  public void onPerformanceStart() {
    offset = microsecondPositionProvider.getMicroseconds();
  }

  @Override
  public void onPerformanceStop() {
    // No operation.
  }
}
