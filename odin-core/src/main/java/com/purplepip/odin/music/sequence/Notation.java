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

package com.purplepip.odin.music.sequence;

import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Sequences;
import com.purplepip.odin.sequence.SpecialisedSequence;
import lombok.ToString;

@ToString(callSuper = true)
public class Notation extends GenericSequence implements SpecialisedSequence {
  private String format;
  private String notation;

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  public Sequence copy() {
    Notation copy = new Notation(this.getId());
    Sequences.copyCoreValues(this, copy);

    copy.setNotation(this.getNotation());
    copy.setFormat(this.getFormat());
    this.getLayers().forEach(copy::addLayer);
    return copy;
  }

  public Notation() {
    super();
  }

  public Notation(long id) {
    super(id);
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getFormat() {
    return format;
  }

  public void setNotation(String notation) {
    this.notation = notation;
  }

  public String getNotation() {
    return notation;
  }

  @Override
  public boolean isEmpty() {
    return notation == null || notation.length() == 0 || super.isEmpty();
  }
}
