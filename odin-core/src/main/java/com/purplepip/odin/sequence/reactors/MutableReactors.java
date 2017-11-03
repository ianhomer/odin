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

package com.purplepip.odin.sequence.reactors;

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequence.conductor.Conductor;
import com.purplepip.odin.sequence.triggers.TriggerConfiguration;
import com.purplepip.odin.sequencer.SequenceTrack;
import com.purplepip.odin.sequencer.Track;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutableReactors extends MutableThings<Reactor> {
  private Map<String, TriggerReactor> triggerReactors = new HashMap<>();

  /**
   * Refresh the bag of reactors.
   *
   * @param triggerStream   trigger stream to use to do the refresh
   * @param reactorSupplier supplier of new reactors
   */
  public void refresh(Supplier<Stream<TriggerConfiguration>> triggerStream,
                      Supplier<TriggerReactor> reactorSupplier,
                      Things<Conductor> conductors, Things<Track> tracks) {
    removeIf(reactor -> triggerStream.get()
        .noneMatch(trigger -> trigger.getId() == reactor.getValue().getId()));

    triggerStream.get().forEach(triggerConfiguration -> {
      /*
       * Add reactor if not present in conductors.
       */
      Optional<TriggerReactor> existing =
          stream().filter(o -> o instanceof TriggerReactor).map(o -> (TriggerReactor) o)
              .filter(reactor -> reactor.getId() == triggerConfiguration.getId()).findFirst();

      TriggerReactor reactor;
      if (existing.isPresent()) {
        reactor = existing.get();
        if (reactor.getTrigger().equals(triggerConfiguration)) {
          LOG.debug("Trigger {} already added and unchanged", triggerConfiguration);
        } else {
          LOG.debug("Updating reactor for {}", triggerConfiguration);
          incrementUpdatedCount();
          reactor.setTriggerConfiguration(triggerConfiguration);
        }
      } else {
        LOG.debug("Creating new reactor for {}", triggerConfiguration);
        reactor = reactorSupplier.get();
        reactor.setTriggerConfiguration(triggerConfiguration);
        add(reactor);
      }

      /*
       * Add all sequence actions.  We currently just clear and re-add.
       */
      reactor.clearTrackActions();
      tracks.stream().forEach(track ->
          track.getTriggers().entrySet().stream()
            .filter(entry -> entry.getKey().equals(triggerConfiguration.getName()))
            .forEach(entry -> reactor.addTrackAction(track, entry.getValue()))
      );

      /*
       * Inject dependent sequences into trigger.
       */
      reactor.getTrigger().dependsOn().forEach(sequenceName -> {
        Track track = tracks.findByName(sequenceName);
        if (track == null) {
          LOG.error("Cannot find track called {} as referenced by trigger {}", sequenceName,
              reactor.getTrigger());
        } else {
          if (track instanceof SequenceTrack) {
            reactor.getTrigger().inject(((SequenceTrack) track).getSequence());
          } else {
            throw new OdinRuntimeException(
                "Referenced track " + track + " called " + sequenceName + " as referenced by "
                    + reactor.getTrigger() + " is not a sequence track");
          }
        }
      });
    });
  }

  @Override
  public boolean add(Reactor reactor) {
    /*
     * Store trigger reactors in a separate hash map for easy access, since these
     * are the only one used (at the moment).
     */
    if (reactor instanceof TriggerReactor) {
      triggerReactors.put(reactor.getName(), (TriggerReactor) reactor);
    }
    return super.add(reactor);
  }

  public Stream<TriggerReactor> triggerStream() {
    return triggerReactors.values().stream();
  }

}
