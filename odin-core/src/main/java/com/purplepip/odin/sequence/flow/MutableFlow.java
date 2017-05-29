package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.sequence.Sequence;

/**
 * Mutable flow.
 */
public interface MutableFlow<S extends Sequence<A>, A> extends Flow<S, A> {
  void setSequence(S sequence);
}
