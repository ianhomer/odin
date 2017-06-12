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

import com.purplepip.odin.music.Note;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.BeatsPerMinute;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.flow.FlowFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Odin Sequencer configuration.
 */
public interface OdinSequencerConfiguration {
  ProjectContainer getProjectContainer();

  BeatsPerMinute getBeatsPerMinute();

  MeasureProvider getMeasureProvider();

  OperationReceiver getOperationReceiver();

  MicrosecondPositionProvider getMicrosecondPositionProvider();

  FlowFactory<Note> getFlowFactory();

  long getClockStartRoundingFactor();

  /*
   * Get clock start offset.  This is can be used as a count in period.  Technically it can
   * give time for the processors to start and determine first events before the clock starts.
   */
  long getClockStartOffset();
}
