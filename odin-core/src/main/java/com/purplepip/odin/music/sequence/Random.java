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

import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.specificity.Name;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Random sequence.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Name("random")
public class Random extends Pattern {
  private int lowerLimit;
  private int upperLimit;

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public Random copy() {
    return copy(new Random(), this);
  }

  protected Random copy(Random copy, Random original) {
    copy.lowerLimit = original.lowerLimit;
    copy.upperLimit = original.upperLimit;
    super.copy(copy, original);
    return copy;
  }

  /**
   * Initialisation after properties are set.
   */
  @Override
  public void afterPropertiesSet() {
    LOG.debug("Initialising random sequence {}", this);

    /*
     * Note that nextInt treats bound as exclusive.
     */
    int number = ThreadLocalRandom.current().nextInt(lowerLimit, upperLimit + 1);
    setNote(new DefaultNote(number, getNote().getVelocity(), getNote().getDuration()));
  }
}
