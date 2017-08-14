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
import com.purplepip.odin.sequence.Sequence;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MutableTracks extends MutableThings<Track> {
  void refresh(Supplier<Stream<Sequence>> sequenceStream, Supplier<SequenceTrack> trackSupplier) {
    removeIf(track -> sequenceStream.get()
        .noneMatch(sequence -> sequence.getId() == track.getId()));

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
        }
      } else {
        LOG.debug("Creating new track for {}", sequence);
        sequenceTrack = trackSupplier.get();
        add(sequenceTrack);
      }

      if (sequenceTrack != null) {
        /*
         * Update sequence in new or modified track.
         */
        sequenceTrack.getSequenceRoll().setSequence(sequence.copy());
      }
    });
  }
}
