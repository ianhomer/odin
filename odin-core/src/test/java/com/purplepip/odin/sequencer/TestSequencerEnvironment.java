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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.DefaultMicrosecondPositionProvider;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.flow.DefaultFlowConfiguration;
import com.purplepip.odin.sequence.measure.StaticBeatMeasureProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSequencerEnvironment {
  private ProjectContainer container = new ProjectContainer(new TransientProject());

  private OdinSequencerConfiguration configuration;
  private OdinSequencer sequencer;

  private SequenceFactory<Note> sequenceFactory;

  public TestSequencerEnvironment(OperationReceiver operationReceiver) throws OdinException {
    initialiseSequencer(operationReceiver);
  }

  private void initialiseSequencer(OperationReceiver operationReceiver) throws OdinException {
    DefaultFlowConfiguration flowConfiguration = new DefaultFlowConfiguration();
    flowConfiguration.setMaxForwardScan(1000000);


    sequenceFactory = SequenceFactory.createNoteSequenceFactory(flowConfiguration);
    configuration = new DefaultOdinSequencerConfiguration()
        .setSequenceFactory(sequenceFactory)
        .setMeasureProvider(new StaticBeatMeasureProvider(4))
        .setBeatsPerMinute(new StaticBeatsPerMinute(12000))
        .setClockStartOffset(10000)
        .setClockStartRoundingFactor(1000)
        .setMicrosecondPositionProvider(new DefaultMicrosecondPositionProvider())
        .setOperationReceiver(operationReceiver);
    sequencer = new OdinSequencer(configuration);
    container.addApplyListener(sequencer);
  }

  public OdinSequencer getSequencer() {
    return sequencer;
  }

  public ProjectContainer getContainer() {
    return container;
  }

  public OdinSequencerConfiguration getConfiguration() {
    return configuration;
  }

  public SequenceFactory<Note> getSequenceFactory() {
    return sequenceFactory;
  }

  /**
   * Start the environment.
   */
  public void start() {
    LOG.debug("Test environment starting");
    container.apply();
    sequencer.start();
    LOG.debug("... test environment started");
  }

  /**
   * Stop the environment.
   */
  public void stop() {
    sequencer.stop();
  }

}
