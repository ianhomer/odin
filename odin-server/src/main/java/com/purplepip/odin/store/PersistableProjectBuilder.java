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

package com.purplepip.odin.store;

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequencer.Channel;
import com.purplepip.odin.sequencer.ProjectBuilder;
import com.purplepip.odin.store.domain.PersistableChannel;
import com.purplepip.odin.store.domain.PersistableLayer;
import com.purplepip.odin.store.domain.PersistableMetronome;
import com.purplepip.odin.store.domain.PersistableNotation;
import com.purplepip.odin.store.domain.PersistableNote;
import com.purplepip.odin.store.domain.PersistablePattern;
import com.purplepip.odin.store.domain.PersistableTick;

/**
 * Sequence builder that updates the project with persistable domain objects.
 */
public class PersistableProjectBuilder extends ProjectBuilder {
  public PersistableProjectBuilder(ProjectContainer projectContainer) {
    super(projectContainer);
  }

  @Override
  protected PersistableLayer createLayer() {
    return new PersistableLayer();
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
  protected Note createNote(int number, int velocity, Real duration) {
    PersistableNote persistableNote = new PersistableNote();
    if (duration instanceof Rational) {
      persistableNote.setNumerator(((Rational) duration).getNumerator());
      persistableNote.setDenominator(((Rational) duration).getDenominator());
    } else {
      // A good enough approximation, given that we'll mostly be dealing with rationals.
      persistableNote.setNumerator(duration.floor());
      persistableNote.setDenominator(1);
    }
    persistableNote.setVelocity(velocity);
    persistableNote.setNumber(number);
    persistableNote.afterLoad();
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
  protected Notation createNotation() {
    return new PersistableNotation();
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
