package com.purplepip.odin.sequence;

/**
 * Mutable sequence.
 */
public interface MutableSequence extends Sequence {
  void setTick(Tick tick);

  void setLength(long length);

  void setOffset(long offset);

  void setChannel(int channel);

  void setFlowName(String flowName);
}
