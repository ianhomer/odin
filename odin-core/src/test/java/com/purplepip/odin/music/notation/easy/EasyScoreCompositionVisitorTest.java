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

package com.purplepip.odin.music.notation.easy;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

public class EasyScoreCompositionVisitorTest {
  @Test
  public void testWriter() {
    Map<String, String> notations = new LinkedHashMap<>();

    notations.put("C#5/q, B4, A4, G#4", null);
    notations.put("C5/q, B4, A4, G#4", null);
    /*
     * Test flats come out as sharps by default (with default key signature)
     */
    notations.put("C5/q, Bb4, A4, G#4", "C5/q, A#4, A4, G#4");
    /*
     * Test octave value is carried over from previous note
     */
    notations.put("C5/q, B4, A, G#", "C5/q, B4, A4, G#4");
    notations.put("C5/q, B4, A", "C5/q, B4, A4, B4/q/r");

    /*
     * Test rests are automatically added to pad out the measure
     */
    notations.put("C5/q, B4, A/8", "C5/q, B4, A4/8, B4/q/r, B4/8/r");

    for (Map.Entry<String, String> entry : notations.entrySet()) {
      Composition composition = new NaturalScoreCompositionFactory().create(entry.getKey());
      String expectedValue = entry.getValue() == null ? entry.getKey() : entry.getValue();
      new EasyScoreCompositionVisitor(composition).visit();
      assertEquals("4/4:" + expectedValue, toNotationString(composition));
    }
  }

  private String toNotationString(Composition composition) {
    StringBuilder builder = new StringBuilder(128);
    composition.stream().forEachOrdered(measure -> {
      if (builder.length() > 0) {
        builder.append('|');
      }
      builder.append(measure.getTime()).append(":");
      measure.stream().forEachOrdered(staff ->
          staff.stream().forEachOrdered(voice -> builder.append(voice.getNotation()))
      );
    });
    return builder.toString();
  }
}