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

/**
 * Note number, based on MIDI standard.
 */
public class NoteNumber {
  private int value;

  /**
   * Create a note number
   *
   * @param letter note letter
   * @param intonation e.g. sharp or flat
   * @param octave octave, 0 to 10
   */
  public NoteNumber(Letter letter, int intonation, int octave) {
    value = octave * 12 + letter.getValue() + intonation;
  }

  public int getValue() {
    return value;
  }
}
