package com.purplepip.odin.sequence;

/**
 * Events that occur over time.
 */
public interface SequenceRuntime<A> {
  Event<A> peek();

  Event<A> pop();

  Tick getTick();
}
