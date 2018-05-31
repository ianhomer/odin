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

package com.purplepip.odin.experiments;

import static com.purplepip.odin.music.notes.Notes.newNote;

import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.action.InitialiseAction;
import com.purplepip.odin.creation.action.StartAction;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.creation.triggers.SequenceStartTrigger;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.BeanyPerformanceBuilder;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.DefaultOperationTransmitter;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.OperationTransmitter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchNoteExperiment {
  public static void main(String[] args) throws InterruptedException {
    MatchNoteExperiment experiment = new MatchNoteExperiment();
    experiment.doExperiment();
  }


  private void doExperiment() throws InterruptedException {
    final CountDownLatch lock = new CountDownLatch(800);

    OperationHandler operationReceiver = (operation, time) -> {
      lock.countDown();
    };

    LOG.info("Creating sequence");
    OdinSequencer sequencer = null;
    MidiDeviceWrapper midiDeviceWrapper = new MidiDeviceWrapper();

    MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
    OperationTransmitter transmitter = new DefaultOperationTransmitter();
    midiDeviceWrapper.registerWithTransmitter(transmitter);
    OdinSequencerConfiguration configuration = new DefaultOdinSequencerConfiguration()
        .setBeatsPerMinute(new StaticBeatsPerMinute(60))
        .setMeasureProvider(measureProvider)
        .setOperationReceiver(
            new OperationReceiverCollection(
                new MidiOperationReceiver(midiDeviceWrapper),
                operationReceiver)
        )
        .setOperationTransmitter(transmitter)
        .setMicrosecondPositionProvider(midiDeviceWrapper.getReceivingDevice());
    try {
      sequencer = new OdinSequencer(configuration);
      SynthesizerHelper synthesizerHelper;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
      }
      DefaultPerformanceContainer container =
          new DefaultPerformanceContainer(new TransientPerformance());
      new BeanyPerformanceBuilder(container)
          .addLayer("groove")
          .withLayers("groove")
          .withLength(-1).withOffset(0)
          .withChannel(9).changeProgramTo("TR-909")
          .withChannel(1).changeProgramTo("piano");

      container.addSequence(new Random()
          .lower(60).upper(72)
          .bits(1).note(newNote())
          .trigger("success-start-trigger", new InitialiseAction())
          .channel(1).layer("groove")
          .tick(Ticks.FOUR_BEAT).name("random"))
          .addTrigger(new PatternNoteTrigger().patternName("random").name("random-note-trigger"))
          .addTrigger(new SequenceStartTrigger()
              .sequenceName("success").name("success-start-trigger"))
          .addSequence(new Notation()
            .notation("C6/8 C# D D# E F")
            .trigger(
                "random-note-trigger", new StartAction())
            .channel(2).layer("groove")
            .enabled(false).length(3).tick(Ticks.HALF)
            .name("success"));

      container.addApplyListener(sequencer);
      container.apply();

      sequencer.prepare();
      sequencer.start();

      // TODO : PatternNoteTrigger current has a version of pattern injected that does not
      // pick up changes to pattern, e.g. after note change after reset

      lock.await(60, TimeUnit.SECONDS);
    } finally {
      LOG.info("... stopping");
      if (sequencer != null) {
        sequencer.stop();
        sequencer.shutdown();
      }
      midiDeviceWrapper.close();
      LOG.debug("Metrics created : {}", configuration.getMetrics().getNames());
    }
  }
}
