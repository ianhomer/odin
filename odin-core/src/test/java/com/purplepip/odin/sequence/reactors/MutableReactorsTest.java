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

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.conductor.MutableConductors;
import com.purplepip.odin.sequence.triggers.TriggerFactory;
import com.purplepip.odin.sequencer.MutableTracks;
import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;

public class MutableReactorsTest {
  private TriggerFactory triggerFactory = TriggerFactory.createTriggerFactory();

  @Test
  public void testRefresh() {
    MutableReactors reactors = new MutableReactors();
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withName("trigger1").withNote(60).addNoteTrigger();
    reactors.refresh(() -> project.getTriggers().stream(), this::createReactor,
        new MutableConductors(), new MutableTracks());
    assertEquals(1, reactors.getStatistics().getAddedCount());
    /*
     * Verify access OK via UnmodifiableReactors
     */
    UnmodifiableReactors unmodifiableReactors = new UnmodifiableReactors(reactors);
    assertEquals(1, unmodifiableReactors.getStatistics().getAddedCount());
    assertEquals("trigger1", unmodifiableReactors.findByName("trigger1").getName());
  }

  private TriggerReactor createReactor() {
    return new TriggerReactor(triggerFactory);
  }
}