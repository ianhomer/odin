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

package com.purplepip.odin.creation.reactors;

import com.purplepip.odin.bag.Things;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.plugin.AbstractPluggableAspects;
import com.purplepip.odin.creation.track.SequenceTrack;
import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TriggerReactors extends AbstractPluggableAspects<TriggerReactor,
    TriggerConfiguration> {
  private Things<Conductor> conductors;
  private Things<Track> tracks;

  public TriggerReactors(Things<Track> tracks, Things<Conductor> conductors) {
    this.conductors = conductors;
    this.tracks = tracks;
  }

  public void refresh(Stream<TriggerConfiguration> configurationStream,
                      Supplier<TriggerReactor> aspectSupplier) {
    super.refresh(configurationStream, aspectSupplier);
    applyChanges();
  }

  /*
   * Apply changes in the reactors with conductors and tracks.
   */
  private void applyChanges() {
    stream().forEach(reactor -> {

      /*
       * Add all sequence actions.  We currently just clear and re-add.
       */
      reactor.clearTrackActions();
      tracks.stream().forEach(track ->
          track.getTriggers().entrySet().stream()
              .filter(entry -> entry.getKey().equals(reactor.getName()))
              .forEach(entry -> reactor.addTrackAction(track, entry.getValue()))
      );

      /*
       * Inject dependent sequences into trigger.
       */
      reactor.dependsOn().forEach(sequenceName -> {
        Track track = tracks.findByName(sequenceName);
        if (track == null) {
          LOG.error("Cannot find track called {} as referenced by aspect {}", sequenceName,
              reactor);
        } else {
          if (track instanceof SequenceTrack) {
            reactor.inject(((SequenceTrack) track).getSequence());
          } else {
            throw new OdinRuntimeException(
                "Referenced track " + track + " called " + sequenceName + " as referenced by "
                    + reactor + " is not a sequence track");
          }
        }
      });
    });
  }
}
