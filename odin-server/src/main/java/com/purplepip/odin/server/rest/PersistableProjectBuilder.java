/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.server.rest;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequencer.Channel;
import com.purplepip.odin.sequencer.ProjectBuilder;
import com.purplepip.odin.server.rest.domain.PersistableChannel;
import com.purplepip.odin.server.rest.domain.PersistableMetronome;
import com.purplepip.odin.server.rest.domain.PersistableNote;
import com.purplepip.odin.server.rest.domain.PersistablePattern;
import com.purplepip.odin.server.rest.domain.PersistableTick;

/**
 * Sequence builder that updates the project with persistable domain objects.
 */
public class PersistableProjectBuilder extends ProjectBuilder {
  public PersistableProjectBuilder(ProjectContainer projectContainer) {
    super(projectContainer);
  }

  @Override
  protected Metronome createMetronome() {
    return new PersistableMetronome();
  }

  /**
   * Create a persistable note.
   *
   * @return note
   */
  @Override
  protected Note createNote(int note, int velocity, long duration) {
    PersistableNote persistableNote = new PersistableNote();
    persistableNote.setDuration(duration);
    persistableNote.setVelocity(velocity);
    persistableNote.setNote(note);
    return persistableNote;
  }

  /**
   * Create a persistable pattern.
   *
   * @return pattern
   */
  @Override
  protected Pattern createPattern() {
    return new PersistablePattern();
  }

  @Override
  protected Channel createChannel() {
    return new PersistableChannel();
  }

  @Override
  protected Tick createTick(Tick tick) {
    return new PersistableTick(tick);
  }
}
