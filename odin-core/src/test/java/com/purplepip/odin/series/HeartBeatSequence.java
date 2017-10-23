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

package com.purplepip.odin.series;

import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.SequenceConfiguration;

public class HeartBeatSequence extends GenericSequence {
  public HeartBeatSequence(long id) {
    super(id);
  }

  @Override
  public SequenceConfiguration copy() {
    HeartBeatSequence copy = new HeartBeatSequence(this.getId());
    copy.setChannel(this.getChannel());
    copy.setFlowName(this.getFlowName());
    copy.setLength(this.getLength());
    copy.setOffset(this.getOffset());
    copy.setProject(this.getProject());
    copy.setTick(this.getTick());
    return copy;
  }
}
