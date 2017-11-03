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

import static com.purplepip.odin.music.notes.Notes.newNote;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.conductor.MutableConductors;
import com.purplepip.odin.sequence.triggers.NoteTrigger;
import com.purplepip.odin.sequence.triggers.TriggerFactory;
import com.purplepip.odin.sequencer.MutableTracks;
import org.junit.Test;

public class MutableReactorsTest {
  private TriggerFactory triggerFactory = TriggerFactory.createTriggerFactory();

  @Test
  public void testRefresh() {
    NoteTrigger trigger = new NoteTrigger();
    trigger.setName("trigger");
    trigger.setNote(newNote(60));
    TransientProject project = new TransientProject();
    project.addTrigger(trigger);
    MutableReactors reactors = new MutableReactors();
    reactors.refresh(() -> project.getTriggers().stream(), this::createReactor,
        new MutableConductors(), new MutableTracks());
    assertEquals(1, reactors.getStatistics().getAddedCount());
    /*
     * Verify access OK via UnmodifiableReactors
     */
    UnmodifiableReactors unmodifiableReactors = new UnmodifiableReactors(reactors);
    assertEquals(1, unmodifiableReactors.getStatistics().getAddedCount());
    assertEquals("trigger", unmodifiableReactors.findByName("trigger").getName());
  }

  @Test
  public void testInjection() {
    MutableReactors reactors = new MutableReactors();
    TransientProject project = new TransientProject();
    // TODO : Implement test
  }

  private TriggerReactor createReactor() {
    return new TriggerReactor(triggerFactory);
  }
}