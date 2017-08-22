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

package com.purplepip.odin.music.sequence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Sequences;

public interface Notation extends MutableSequence {
  @Override
  default Sequence copy() {
    Notation copy = new DefaultNotation(this.getId());
    Sequences.copyCoreValues(this, copy);

    copy.setNotation(this.getNotation());
    copy.setFormat(this.getFormat());
    this.getLayers().forEach(copy::addLayer);
    return copy;
  }

  void setFormat(String format);

  @JsonProperty(defaultValue = "natural")
  String getFormat();

  void setNotation(String notation);

  String getNotation();
}
