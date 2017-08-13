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

package com.purplepip.odin.music.notes;

import com.purplepip.odin.common.OdinRuntimeException;
import java.util.HashMap;
import java.util.Map;

/**
 * Note name cache for the C0 to G10 for the MIDI numbers 0 to 127.
 */
public class NoteNameCache {
  private static final Map<Integer, String> sharpCache = new HashMap<>();
  private static final Map<Integer, String> flatCache = new HashMap<>();

  private static void cache(Letter letter, int intonation, int octave) {
    int number = new NoteNumber(letter, intonation, octave).getValue();
    if (intonation == 0) {
      String name = letter.name() + octave;
      sharpCache.put(number, name);
      flatCache.put(number, name);
    } else if (intonation == -1) {
      String name = letter.name() + "b" + octave;
      flatCache.put(number, name);
    } else if (intonation == 1) {
      String name = letter.name() + "#" + octave;
      sharpCache.put(number, name);
    } else {
      throw new OdinRuntimeException(
          "Cannot create note name cache entry with intonation " + intonation);
    }
  }

  static {
    for (Letter letter : Letter.values()) {
      for (int octave = -1 ; octave < 10 ; octave ++) {
        cache(letter, 0, octave);
        switch (letter) {
          case A:
            cache(letter, -1, octave);
            cache(letter, 1, octave);
            break;
          case B:
            cache(letter, -1, octave);
            break;
          case C:
            cache(letter, 1, octave);
            break;
          case D:
            cache(letter, -1, octave);
            cache(letter, 1, octave);
            break;
          case E:
            cache(letter, -1, octave);
            break;
          case F:
            cache(letter, 1, octave);
            break;
          case G:
            cache(letter, -1, octave);
            if (octave < 10) {
              cache(letter, 1, octave);
            }
            break;
          default:
            throw new OdinRuntimeException("Letter not recognised : " + letter);
        }
      }
    }
  }

  /**
   * Get note name for the given number.  Sharps are always used for the intonation.
   *
   * @param number note number
   * @return note name
   */
  public String getName(int number) {
    return getName(number, true);
  }

  /**
   * Get note name for the given number.
   *
   * @param number note number
   * @param useSharps if true then use sharps for intonation, otherwise use flats
   * @return note name
   */
  public String getName(int number, boolean useSharps) {
    String name = useSharps ? sharpCache.get(number) : flatCache.get(number);
    if (name == null) {
      throw new OdinRuntimeException("MIDI note " + number + " does not exist");
    }
    return name;
  }
}
