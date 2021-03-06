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

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Wholes;

/**
 * Notes utility classes.
 */
public final class Notes {
  public static final int DEFAULT_NUMBER = 60;
  public static final int DEFAULT_VELOCITY = 40;
  public static final Rational DEFAULT_DURATION = Wholes.ONE;

  private Notes() {
  }

  public static DefaultNote newNote() {
    return new DefaultNote(DEFAULT_NUMBER, DEFAULT_VELOCITY, DEFAULT_DURATION);
  }

  public static DefaultNote newNote(int number) {
    return new DefaultNote(number, DEFAULT_VELOCITY, DEFAULT_DURATION);
  }

  public static DefaultNote newNote(int number, int velocity) {
    return new DefaultNote(number, velocity, DEFAULT_DURATION);
  }

  public static DefaultNote newNote(int number, int velocity, int duration) {
    return new DefaultNote(number, velocity, Wholes.valueOf(duration));
  }
}
