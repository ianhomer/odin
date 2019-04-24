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

package com.purplepip.odin.boot;

import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.midix.MidiDevice;
import com.purplepip.odin.midix.MidiHandle;
import com.purplepip.odin.midix.MidiTransmitterBinding;
import com.purplepip.odin.midix.SynthesizerDevice;
import com.purplepip.odin.sequencer.DefaultOperationTransmitter;
import com.purplepip.odin.sequencer.OperationTransmitter;
import java.util.Optional;

/** MIDI application where sink and / or source are optional. */
public class OptionalMidiApplication implements AutoCloseable {
  private final MidiDevice source;
  private final MidiDevice sink;
  private final MidiTransmitterBinding binding;
  private final OperationTransmitter transmitter;

  /**
   * Create MIDI application.
   */
  public OptionalMidiApplication(Environment environment) {
    source = environment.findOneSource(MidiHandle.class).orElse(null);
    sink = environment.findOneSink(MidiHandle.class).orElse(null);
    binding = new MidiTransmitterBinding();
    transmitter = new DefaultOperationTransmitter();
    if (source != null) {
      binding.bind(source, transmitter);
    }
  }

  /**
   * Get synthesizer device.
   *
   * @return synthesizer
   */
  public Optional<SynthesizerDevice> getSynthesizer() {
    return sink instanceof SynthesizerDevice
        ? Optional.of((SynthesizerDevice) sink)
        : Optional.empty();
  }

  public Optional<MidiDevice> getSource() {
    return Optional.ofNullable(source);
  }

  public Optional<MidiDevice> getSink() {
    return Optional.ofNullable(sink);
  }

  public OperationTransmitter getTransmitter() {
    return transmitter;
  }

  @Override
  public void close() {
    sink.close();
    source.close();
    binding.close();
  }
}
