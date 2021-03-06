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

package com.purplepip.odin.store;

import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.sequence.MutableSequenceConfiguration;
import com.purplepip.odin.creation.triggers.MutableTriggerConfiguration;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.sequencer.BeanyPerformanceBuilder;
import com.purplepip.odin.store.domain.PersistableChannel;
import com.purplepip.odin.store.domain.PersistableLayer;
import com.purplepip.odin.store.domain.PersistableNote;
import com.purplepip.odin.store.domain.PersistableSequence;
import com.purplepip.odin.store.domain.PersistableTick;
import com.purplepip.odin.store.domain.PersistableTrigger;

/**
 * Sequence builder that updates the performance with persistable domain objects.
 */
public class PersistablePerformanceBuilder extends BeanyPerformanceBuilder {
  public PersistablePerformanceBuilder(DefaultPerformanceContainer performanceContainer) {
    super(performanceContainer);
  }

  @Override
  protected PersistableLayer createLayer() {
    return new PersistableLayer();
  }

  @Override
  protected MutableTriggerConfiguration createTrigger(String type) {
    PersistableTrigger triggerConfiguration = new PersistableTrigger();
    triggerConfiguration.setType(type);
    return triggerConfiguration;
  }

  @Override
  protected MutableSequenceConfiguration createSequence(String type) {
    PersistableSequence persistableSequence = new PersistableSequence();
    persistableSequence.setType(type);
    return persistableSequence;
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

  @Override
  protected Channel createChannel() {
    return new PersistableChannel();
  }

  @Override
  protected Tick createTick(Tick tick) {
    return new PersistableTick(tick);
  }
}
