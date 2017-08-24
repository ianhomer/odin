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

package com.purplepip.odin.music.notation;

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Whole;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic reference lookups that can be used by any specific notation reference lookup.
 */
public abstract class AbstractReference {
  private static final Map<String, Integer> accidentals = new HashMap<>();
  private static final Map<String, Rational> durations = new HashMap<>();
  private static final Map<Rational, String> durationsReverse = new HashMap<>();

  static {
    accidentals.put("#", 1);
    accidentals.put("b", -1);
    accidentals.put("n", 0);

    durations.put("h", Whole.valueOf(2));
    durations.put("q", Whole.valueOf(1));
    durations.put("8", Rational.valueOf(1,2));

    durations.forEach((key, value) -> durationsReverse.put(value, key));
  }

  public int getAccidentalIncrement(String accidental) {
    return accidentals.get(accidental);
  }

  public Rational getDurationLength(String duration) {
    return durations.get(duration);
  }

  /**
   * Duration label.
   *
   * @param length length of note
   * @return how the duration should be written
   */
  public String getDurationLabel(Rational length) {
    String label = durationsReverse.get(length);
    if (label == null) {
      return length.toString();
    }
    return label;
  }
}
