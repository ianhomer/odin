package com.purplepip.odin.sequence;

/**
 * Persistable sequence configuration.
 */
public interface Sequence<A> {
  Tick getTick();

  long getLength();

  long getOffset();

  int getChannel();

  String getFlowName();
}
