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

import static org.junit.Assert.assertEquals;

import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.reactors.MutableReactors;
import com.purplepip.odin.sequence.reactors.TriggerReactor;
import javax.sound.midi.ShortMessage;
import org.junit.Test;

public class SequencerReceiverTest {
  @Test
  public void send() throws Exception {
    MutableReactors reactors = new MutableReactors();
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withName("trigger1").withNote(60).addNoteTrigger();
    reactors.refresh(() -> project.getTriggers().stream(), TriggerReactor::new);
    MetricRegistry metricRegistry = new MetricRegistry();
    SequencerReceiver receiver = new SequencerReceiver(reactors, metricRegistry);
    receiver.send(new ShortMessage(144, 60, 2), -1);
    assertEquals(1, metricRegistry
        .meter("receiver.triggered").getCount());
  }
}