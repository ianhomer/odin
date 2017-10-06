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
import com.purplepip.odin.sequence.reactors.MutableReactors;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequencerReceiver implements Receiver {
  private MutableReactors reactors;
  private Meter triggered;

  public SequencerReceiver(MutableReactors reactors, MetricRegistry metrics) {
    this.reactors = reactors;
    triggered = metrics.meter("receiver.triggered");
  }

  @Override
  public void send(final MidiMessage message, long time) {
    LOG.debug("MIDI message received {} from {} at {}", message.getMessage(), time);
    reactors.messageTriggerStream()
        .forEach(reactor -> {
          if (reactor.getMessageTrigger().matches(message.getMessage())) {
            LOG.debug("Trigger {} triggered", reactor.getTrigger());
            triggered.mark();
          }
        });
  }

  @Override
  public void close() {
    LOG.debug("Closing {}", this);
  }
}
