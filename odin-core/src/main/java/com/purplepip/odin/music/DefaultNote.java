/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.music;

import lombok.ToString;

/**
 * Default Note.
 */
@ToString
public class DefaultNote implements Note {
  private int number;
  private int velocity;
  private long duration;
  private long denominator;

  /**
   * Create a default note.
   *
   * @param number Number of note
   * @param velocity Velocity of note
   * @param duration Duration of note
   */
  public DefaultNote(int number, int velocity, long duration) {
    this(number, velocity, duration, 1);
  }

  /**
   * Create a default note.
   *
   * @param number Number of note
   * @param velocity Velocity of note
   * @param duration Duration of note
   * @param denominator Duration denominator of note
   */
  public DefaultNote(int number, int velocity, long duration, long denominator) {
    this.number = number;
    this.velocity = velocity;
    this.duration = duration;
    this.denominator = denominator;
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
  public long getDuration() {
    return duration;
  }

  @Override
  public long getDenominator() {
    return denominator;
  }
}
