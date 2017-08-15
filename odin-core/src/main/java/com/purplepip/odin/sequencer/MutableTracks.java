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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.bag.UnmodifiableThings;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.conductor.Conductor;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MutableTracks extends MutableThings<Track> {
  void refresh(Supplier<Stream<Sequence>> sequenceStream, Supplier<SequenceTrack> trackSupplier,
               UnmodifiableThings<Conductor> conductors) {
    removeIf(track -> sequenceStream.get()
        .noneMatch(sequence -> sequence.getId() == track.getValue().getId()));

    sequenceStream.get().forEach(sequence -> {
      /*
       * Add sequence if not present in tracks.
       */
      Optional<SequenceTrack> existingTrack =
          stream()
              .filter(o -> o instanceof SequenceTrack)
              .map(o -> (SequenceTrack) o)
              .filter(track -> sequence.getId() == track.getSequence().getId()).findFirst();

      SequenceTrack sequenceTrack = null;
      if (existingTrack.isPresent()) {
        if (existingTrack.get().getSequence().equals(sequence)) {
          LOG.debug("Sequence {} already added and unchanged", sequence);
        } else {
          LOG.debug("Updating track for {}", sequence);
          incrementUpdatedCount();
          sequenceTrack = existingTrack.get();
          sequenceTrack.setCopyOfSequence(sequence);
        }
      } else {
        LOG.debug("Creating new track for {}", sequence);
        sequenceTrack = trackSupplier.get();
        sequenceTrack.setCopyOfSequence(sequence);
        add(sequenceTrack);
      }
    });

    /*
     * Binding conductors to tracks.
     * // TODO : This binding is a cheap implementaion for now just remove all bindings and
     * // rebind.  It should be done more efficiently.
     */
    stream()
        .filter(o -> o instanceof SequenceTrack)
        .map(o -> (SequenceTrack) o).forEach(sequenceTrack -> {
          sequenceTrack.unbindConductors();
          sequenceTrack.getSequence().getLayers().stream().forEach(layer ->
              sequenceTrack.bindConductor(conductors.findByName(layer))
          );
        });
  }
}
