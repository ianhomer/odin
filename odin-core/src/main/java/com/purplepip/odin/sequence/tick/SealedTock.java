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

package com.purplepip.odin.sequence.tick;

import com.purplepip.odin.math.Real;
import lombok.ToString;

/**
 * Tock that wraps another, potentially mutable, tock, but cannot mutate the tock itself.
 */
@ToString
public class SealedTock implements Tock {
  private Tock tock;

  public SealedTock(Tock tock) {
    this.tock = tock;
  }

  @Override
  public Tick getTick() {
    return tock.getTick();
  }

  @Override
  public Real getPosition() {
    return tock.getPosition();
  }
}
