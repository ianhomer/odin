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

package com.purplepip.odin.music.notes;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import lombok.EqualsAndHashCode;

/**
 * Default Note.
 */
@EqualsAndHashCode
public class DefaultNote implements Note {
  private final int number;
  private final int velocity;
  private final Real duration;

  /**
   * Create a default note.
   *
   * @param number Number of note
   * @param velocity Velocity of note
   * @param duration Duration of note
   */
  public DefaultNote(int number, int velocity, long duration) {
    this(number, velocity, Whole.valueOf(duration));
  }

  /**
   * Create a default note.
   *
   * @param number Number of note
   * @param velocity Velocity of note
   * @param duration Duration of note
   */
  public DefaultNote(int number, int velocity, Real duration) {
    this.number = number;
    this.velocity = velocity;
    this.duration = duration;
  }

  @Override
  public int getNumber() {
    return number;
  }

  @Override
  public int getVelocity() {
    return velocity;
  }

  @Override
  public Real getDuration() {
    return duration;
  }

  public String toString() {
    return "♪(" + number + "↓" + velocity
        + (duration.equals(Wholes.ONE) ? "" : "~" + duration) + ")";
  }
}
