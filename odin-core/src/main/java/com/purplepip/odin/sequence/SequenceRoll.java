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

package com.purplepip.odin.sequence;

import com.purplepip.odin.sequence.flow.MutableFlow;

/**
 * A roll that is based on a sequence.
 *
 * @param <A> type of object controlled by this roll
 */
public interface SequenceRoll<A> extends Roll<A> {
  void setSequence(Sequence sequence);

  Sequence getSequence();

  void refresh();

  void setFlow(MutableFlow<Sequence, A> flow);

  MutableFlow<Sequence, A> getFlow();

  OffsetProvider getOffsetProvider();
}
