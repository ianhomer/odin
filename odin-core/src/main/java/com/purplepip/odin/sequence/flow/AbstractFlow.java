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

import com.purplepip.odin.math.Real;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import java.lang.reflect.ParameterizedType;

/**
 * Abstract logic class.
 */
public abstract class AbstractFlow<S extends Sequence, A> implements MutableFlow<S, A> {
  private FlowConfiguration configuration;
  private Clock clock;
  private MeasureProvider measureProvider;
  private S sequence;
  private Class<S> sequenceClass;

  @SuppressWarnings("unchecked")
  public AbstractFlow() {
    this.sequenceClass = (Class<S>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  @Override
  public Class<S> getSequenceClass() {
    return sequenceClass;
  }

  @Override
  public void setSequence(S sequence) {
    this.sequence = sequence;
  }

  @Override
  public S getSequence() {
    return sequence;
  }

  @Override
  public void setClock(Clock clock) {
    this.clock = clock;
  }

  @Override
  public Clock getClock() {
    return clock;
  }

  @Override
  public void setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
  }

  @Override
  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  @Override
  public void setConfiguration(FlowConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public FlowConfiguration getConfiguration() {
    return configuration;
  }

  protected Real getMaxScanForward() {
    return getClock().getDuration(getConfiguration().getMaxForwardScan());
  }
}
