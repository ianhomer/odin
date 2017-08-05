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

import com.purplepip.odin.music.composition.Composition;

/**
 * Notation for internal composition structure used only for test assertion purposes.  This
 * notation is simple to serialise for comparison, but not designed for human readability.
 */
class CompositionNotation {
  private String notation;

  CompositionNotation(Composition composition) {
    notation = createNotation(composition);
  }

  private String createNotation(Composition composition) {
    StringBuilder builder = new StringBuilder(128);
    composition.stream().forEachOrdered(event ->
        builder
          .append(getDuration(event.getTime(), event.getDenominator()))
          .append(".")
          .append(getDuration(event.getValue().getDuration(), event.getValue().getDenominator()))
          .append("-")
          .append(event.getValue().getNumber()).append(" ")
    );
    return builder.toString();
  }

  private String getDuration(long numerator, long denominator) {
    if (denominator == 1) {
      return String.valueOf(numerator);
    } else {
      return String.valueOf(numerator) + "/" + String.valueOf(denominator);
    }
  }

  public String getBody() {
    return notation;
  }
}
