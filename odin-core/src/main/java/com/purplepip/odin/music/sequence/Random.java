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
  private int upper;
  private int lower;

  public Random lower(int lower) {
    this.lower = lower;
    return this;
  }

  public Random upper(int upper) {
    this.upper = upper;
    return this;
  }

  /**
   * Set lower and upper note number for random range.
   *
   * @param lower lower limit
   * @param upper upper limit
   * @return this
   */
  public Random range(int lower, int upper) {
    this.upper = upper;
    this.lower = lower;
    return this;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public Random copy() {
    return copy(new Random());
  }

  protected Random copy(Random copy) {
    copy.lower = this.lower;
    copy.upper = this.upper;
    super.copy(copy);
    return copy;
  }

  /**
   * Initialisation after properties are set.
   */
  @Override
  public void initialise() {
    LOG.debug("Initialising random sequence {}", this);
    randomise();
  }

  private void randomise() {
    /*
     * Note that nextInt treats bound as exclusive.
     */
    // TODO : Random variation of properties should be covered by a filter.  There is value in
    // many properties of many plugins to vary, either randomness at initialisation or
    // random variations over time.   This implementation of specific use case for randomness
    // is just an initial lightweight solution.  We MUST not use this as a pattern for other
    // random property values.
    int number = ThreadLocalRandom.current().nextInt(lower, upper + 1);
    setNote(new DefaultNote(number, getNote().getVelocity(), getNote().getDuration()));
  }
}
