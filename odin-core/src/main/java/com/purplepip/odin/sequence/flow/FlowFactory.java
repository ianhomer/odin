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
