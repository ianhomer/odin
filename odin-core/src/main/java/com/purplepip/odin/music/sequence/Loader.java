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

package com.purplepip.odin.music.sequence;

import com.purplepip.odin.clock.Loop;
import com.purplepip.odin.clock.MeasureContext;
import com.purplepip.odin.creation.sequence.SequencePlugin;
import com.purplepip.odin.events.GenericEvent;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.performance.LoadPerformanceOperation;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Load performance sequence.
 */
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@Name("loader")
public class Loader extends SequencePlugin {
  private String performance;

  public Loader performance(String performance) {
    this.performance = performance;
    return this;
  }

  @Override
  public GenericEvent<LoadPerformanceOperation> getNextEvent(MeasureContext context, Loop loop) {
    Real nextTock = loop.getAbsolutePosition().plus(Wholes.ONE);
    if (nextTock.floor() == 0) {
      return new GenericEvent<>(new LoadPerformanceOperation(performance), nextTock);
    }
    return null;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public Loader copy() {
    return copy(new Loader());
  }

  protected Loader copy(Loader copy) {
    super.copy(copy);
    copy.performance = this.performance;
    return copy;
  }
}
