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

package com.purplepip.odin.bag;

import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThingStatistics implements MutableThingStatistics {
  private final AtomicInteger addedCount = new AtomicInteger();
  private final AtomicInteger updatedCount = new AtomicInteger();
  private final AtomicInteger removedCount = new AtomicInteger();

  @Override
  public int getAddedCount() {
    return addedCount.get();
  }

  @Override
  public int getRemovedCount() {
    return removedCount.get();
  }

  @Override
  public int getUpdatedCount() {
    return updatedCount.get();
  }

  @Override
  public void incrementAddedCount() {
    addedCount.incrementAndGet();
  }

  @Override
  public void incrementRemovedCount(int incrementalRemovedCount) {
    removedCount.addAndGet(incrementalRemovedCount);
  }

  @Override
  public void incrementUpdatedCount() {
    updatedCount.incrementAndGet();
  }
}
