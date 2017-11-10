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
import com.purplepip.odin.creation.aspect.Aspects;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.track.SequenceTrack;
import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.creation.triggers.Trigger;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutableReactors extends Aspects<TriggerReactor, TriggerConfiguration, Trigger> {
  /**
   * Bind the reactors with conductors and tracks.
   *
   * @param conductors conductors
   * @param tracks tracks
   */
  public void bind(Things<Conductor> conductors, Things<Track> tracks) {
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
      reactor.getPlugin().dependsOn().forEach(sequenceName -> {
        Track track = tracks.findByName(sequenceName);
        if (track == null) {
          LOG.error("Cannot find track called {} as referenced by trigger {}", sequenceName,
              reactor.getPlugin());
        } else {
          if (track instanceof SequenceTrack) {
            reactor.getPlugin().inject(((SequenceTrack) track).getSequence());
          } else {
            throw new OdinRuntimeException(
                "Referenced track " + track + " called " + sequenceName + " as referenced by "
                    + reactor.getPlugin() + " is not a sequence track");
          }
        }
      });
    });
  }
}
