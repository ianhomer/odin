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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.sequence.SequenceConfiguration;
import com.purplepip.odin.sequence.conductor.Conductor;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutableTracks extends MutableThings<Track> {
  void refresh(Supplier<Stream<SequenceConfiguration>> sequenceStream,
               Supplier<SequenceRollTrack> trackSupplier,
               Things<Conductor> conductors) {
    removeIf(track -> sequenceStream.get()
        .noneMatch(sequence -> sequence.getId() == track.getValue().getId()));

    sequenceStream.get().forEach(sequence -> {
      /*
       * Add sequence if not present in tracks.
       */
      Optional<SequenceRollTrack> existingTrack = stream()
              .filter(o -> o instanceof SequenceRollTrack).map(o -> (SequenceRollTrack) o)
              .filter(track -> sequence.getId() == track.getSequence().getId()).findFirst();

      if (existingTrack.isPresent()) {
        if (existingTrack.get().getSequence().equals(sequence)) {
          LOG.debug("Sequence {} already added and unchanged", sequence);
        } else {
          LOG.debug("Updating track for {}", sequence);
          incrementUpdatedCount();
          existingTrack.get().setCopyOfSequence(sequence);
        }
      } else {
        LOG.debug("Creating new track for {}", sequence);
        SequenceRollTrack sequenceTrack = trackSupplier.get();
        sequenceTrack.setCopyOfSequence(sequence);
        add(sequenceTrack);
      }
    });

    /*
     * Remove empty tracks, i.e. tracks that do not output any events.
     */
    removeIf(track -> track.getValue().isEmpty());

    /*
     * Binding conductors to tracks.
     * // TODO : This binding is a cheap implementation for now just remove all bindings and
     * // rebind.  It should be done more efficiently.
     */
    stream()
        .filter(o -> o instanceof SequenceRollTrack)
        .map(o -> (SequenceRollTrack) o).forEach(sequenceTrack -> {
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
