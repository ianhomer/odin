/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.Sequence;

/**
 * Factory to generate flow object for given sequence.
 */
public class FlowFactory<A> {


  /**
   * Create flow object for the given sequence.
   *
   * @param sequence sequence
   * @return flow
   * @throws OdinException exception
   */
  @SuppressWarnings("unchecked")
  public Flow<Sequence, A> createFlow(Sequence sequence) throws OdinException {
    Class<? extends MutableFlow<Sequence, A>> flowClass;
    try {
      flowClass = (Class<? extends MutableFlow<Sequence, A>>)
          Class.forName(sequence.getFlowName());
    } catch (ClassNotFoundException e) {
      throw new OdinException("Cannot find class " + sequence.getFlowName(), e);
    }
    MutableFlow<Sequence, A> flow;
    try {
      flow = flowClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new OdinException("Cannot create instance of " + flowClass, e);
    }
    flow.setSequence(sequence);
    return flow;
  }
}
