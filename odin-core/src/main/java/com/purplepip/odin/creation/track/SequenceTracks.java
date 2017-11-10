/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
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

package com.purplepip.odin.creation.track;

import com.purplepip.odin.bag.Things;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.plugin.AbstractPluggableAspects;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * Mutable tracks.
 */
@Slf4j
public class SequenceTracks extends AbstractPluggableAspects<SequenceRollTrack,
    SequenceConfiguration> {
  private Things<? extends Conductor> conductors;

  public SequenceTracks(Things<? extends Conductor> conductors) {
    this.conductors = conductors;
  }

  @Override
  protected void afterRefresh() {
    /*
     * Binding conductors to tracks.
     * // TODO : This binding is a cheap implementation for now just remove all bindings and
     * // rebind.  It should be done more efficiently.
     */
    stream().forEach(sequenceTrack -> {
      sequenceTrack.unbindConductors();
      sequenceTrack.getSequence().getLayers().forEach(layer -> {
        Conductor conductor = conductors.findByName(layer);
        if (conductor == null) {
          LOG.warn("Cannot find conductor {} to bind to track {}.  All conductors are {}",
              layer, sequenceTrack.getName(), conductors);
        } else {
          sequenceTrack.bindConductor(conductors.findByName(layer));
        }
      });
    });
  }
}
