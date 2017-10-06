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
import com.purplepip.odin.sequence.triggers.MessageTrigger;
import com.purplepip.odin.sequence.triggers.Trigger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutableReactors extends MutableThings<Reactor> {
  private Map<String, TriggerReactor> messageTriggerReactors = new HashMap<>();

  /**
   * Refresh the bag of reactors.
   *
   * @param triggerStream   trigger stream to use to do the refresh
   * @param reactorSupplier supplier of new reactors
   */
  public void refresh(Supplier<Stream<Trigger>> triggerStream,
                      Supplier<TriggerReactor> reactorSupplier) {
    removeIf(reactor -> triggerStream.get()
        .noneMatch(trigger -> trigger.getId() == reactor.getValue().getId()));

    triggerStream.get().forEach(trigger -> {
      /*
       * Add reactor if not present in conductors.
       */
      Optional<TriggerReactor> existing =
          stream()
              .filter(o -> o instanceof TriggerReactor)
              .map(o -> (TriggerReactor) o)
              .filter(reactor -> reactor.getId() == trigger.getId()).findFirst();

      TriggerReactor reactor;
      if (existing.isPresent()) {
        if (existing.get().getTrigger().equals(trigger)) {
          LOG.debug("Trigger {} already added and unchanged", trigger);
        } else {
          LOG.debug("Updating reactor for {}", trigger);
          incrementUpdatedCount();
          reactor = existing.get();
          reactor.setTrigger(trigger);
        }
      } else {
        LOG.debug("Creating new reactor for {}", trigger);
        reactor = reactorSupplier.get();
        reactor.setTrigger(trigger);
        add(reactor);
      }
    });
  }

  @Override
  public boolean add(Reactor reactor) {
    /*
     * Store message trigger reactors in a separate hash map for easy access, since these
     * are the only one used (at the moment).
     */
    if (reactor instanceof TriggerReactor
        && ((TriggerReactor) reactor).getTrigger() instanceof MessageTrigger) {
      messageTriggerReactors.put(reactor.getName(), (TriggerReactor) reactor);
    }
    return super.add(reactor);
  }

  public Stream<TriggerReactor> messageTriggerStream() {
    return messageTriggerReactors.values().stream();
  }

}
