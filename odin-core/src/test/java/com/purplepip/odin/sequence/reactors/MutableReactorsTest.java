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

import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.conductor.MutableConductors;
import com.purplepip.odin.sequence.track.MutableTracks;
import com.purplepip.odin.sequence.triggers.NoteTrigger;
import com.purplepip.odin.sequence.triggers.PatternNoteTrigger;
import com.purplepip.odin.sequence.triggers.TriggerFactory;
import org.junit.Test;

public class MutableReactorsTest {
  private TriggerFactory triggerFactory = TriggerFactory.newTriggerFactory();

  @Test
  public void testRefresh() {
    TransientProject project = new TransientProject();
    project.addTrigger(new NoteTrigger().note(newNote(60)).name("trigger"));

    MutableReactors reactors = new MutableReactors();
    reactors.refresh(
        () -> project.getTriggers().stream(),
        () -> new TriggerReactor(triggerFactory),
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
    TransientProject project = new TransientProject();
    project.addSequence(new Random().range(60, 72).bits(1).name("random"));
    project.addTrigger(new PatternNoteTrigger().patternName("random").name("trigger"));
    MutableReactors reactors = new MutableReactors();
    MutableTracks tracks = new MutableTracks();
    reactors.refresh(() -> project.getTriggers().stream(),
        () -> new TriggerReactor(triggerFactory),
        new MutableConductors(), tracks);
    // TODO : Complete test.
  }
}