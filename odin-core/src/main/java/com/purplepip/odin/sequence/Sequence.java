package com.purplepip.odin.sequence;

/**
 * Persistable sequence configuration.
 */
public interface Sequence {
  Tick getTick();

  long getLength();

  long getOffset();

  int getChannel();

  String getFlowName();
}
