package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.music.flow.MetronomeFlow;

/**
 * Flow that fails on creation.
 */
public class UnconstructibleFlow extends MetronomeFlow {
  private UnconstructibleFlow() {
  }
}
