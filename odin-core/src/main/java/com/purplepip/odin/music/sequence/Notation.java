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

import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;

public interface Notation extends MutableSequence {
  @Override
  default Sequence copy() {
    Notation copy = new DefaultNotation(this.getId());
    copy.setNotation(this.getNotation());
    copy.setFormat(this.getFormat());
    copy.setChannel(this.getChannel());
    copy.setFlowName(this.getFlowName());
    copy.setLength(this.getLength());
    copy.setOffset(this.getOffset());
    copy.setProject(this.getProject());
    copy.setTick(this.getTick());
    copy.getLayers().forEach(this::addLayer);
    return copy;
  }

  void setFormat(String format);

  String getFormat();

  void setNotation(String notation);

  String getNotation();
}
