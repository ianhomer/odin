package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Flow that fails on creation.
 */
public class UnconstructibleFlow extends MetronomeFlow {
  public UnconstructibleFlow(Clock clock, MeasureProvider measureProvider) {
    super(clock, measureProvider);
  }
}
