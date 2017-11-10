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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.creation.reactors.MutableReactors;
import com.purplepip.odin.creation.triggers.Action;
import lombok.extern.slf4j.Slf4j;

/**
 * Execute triggers based on received operations.
 */
@Slf4j
public class ReactorReceiver implements OperationReceiver {
  private MutableReactors reactors;
  private Meter triggered;

  public ReactorReceiver(MutableReactors reactors, MetricRegistry metrics) {
    this.reactors = reactors;
    triggered = metrics.meter("receiver.triggered");
  }

  @Override
  public void send(Operation operation, long time) {
    LOG.debug("Operation received {} at {}", operation, time);
    reactors.triggerStream()
        .forEach(reactor -> {
          if (reactor.getPlugin().isTriggeredBy(operation)) {
            LOG.debug("Trigger {} triggered", reactor.getPlugin());
            triggered.mark();
            reactor.getTracks().forEach(entry -> {
              LOG.debug("Track {} triggered", entry.getKey());
              Action action = entry.getValue();
              switch (action)  {
                case ENABLE:
                  entry.getKey().setEnabled(true);
                  break;
                case DISABLE:
                  entry.getKey().setEnabled(false);
                  break;
                default:
                  LOG.warn("Trigger action {} not supported", action);
              }
            });
          }
        });
  }
}
