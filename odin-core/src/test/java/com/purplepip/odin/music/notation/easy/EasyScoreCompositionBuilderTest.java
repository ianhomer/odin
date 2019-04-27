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

package com.purplepip.odin.music.notation.easy;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.notation.easy.composition.EasyComposition;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EasyScoreCompositionBuilderTest {
  @Test
  void testWriter() {
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
    notations.put("C5", "C5/q, B4/h/r, B4/q/r");
    notations.put("C5/q, B4", "C5/q, B4, B4/h/r");

    /*
     * Test default octave to middle C
     */
    notations.put("C", "C4/q, B4/h/r, B4/q/r");

    assertNotationsOk(notations);

  }

  @Test
  void testMultipleMeasures() {
    Map<String, String> notations = new LinkedHashMap<>();

    /*
     * Test multiple measures
     */
    notations.put("A B C D E", "A4/q, B4, C4, D4|4/4:E4/q, B4/h/r, B4/q/r");

    assertNotationsOk(notations);
  }

  private void assertNotationsOk(Map<String, String> notations) {
    for (Map.Entry<String, String> entry : notations.entrySet()) {
      EventsComposition eventsComposition =
          new NaturalScoreCompositionFactory().create(entry.getKey());
      String expectedValue = entry.getValue() == null ? entry.getKey() : entry.getValue();
      EasyComposition easyComposition =
          new EasyScoreCompositionBuilder(eventsComposition).build();
      assertEquals("4/4:" + expectedValue, toNotationString(easyComposition));
    }
  }

  private String toNotationString(EasyComposition composition) {
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