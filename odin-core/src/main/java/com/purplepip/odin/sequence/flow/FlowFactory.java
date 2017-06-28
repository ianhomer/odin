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

package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.sequence.Sequence;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to generate flow object for given sequence.
 */
public class FlowFactory<A> {
  private static final Map<String, Class<? extends MutableFlow>> FLOWS = new HashMap<>();

  /*
   * Statically register known flows.  In the future we may design a plugin architecture, but
   * for now it is kept tight by only allowing registered classes.
   */

  static {
    register(PatternFlow.class);
    register(MetronomeFlow.class);
  }

  private static void register(Class<? extends MutableFlow> clazz) {
    FLOWS.put(clazz.getName(), clazz);
  }

  /**
   * Create flow object for the given sequence.
   *
   * @param sequence sequence
   * @return flow
   * @throws OdinException exception
   */
  @SuppressWarnings("unchecked")
  public MutableFlow<Sequence, A> createFlow(Sequence sequence) throws OdinException {
    Class<? extends MutableFlow<Sequence, A>> flowClass;
    flowClass = (Class<? extends MutableFlow<Sequence, A>>)
          FLOWS.get(sequence.getFlowName());
    if (flowClass == null) {
      throw new OdinException("Flow class " + sequence.getFlowName() + " not registered");
    }
    MutableFlow<Sequence, A> flow;
    try {
      flow = flowClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new OdinException("Cannot create instance of " + flowClass, e);
    }
    flow.setSequence(sequence.copy());
    return flow;
  }
}
