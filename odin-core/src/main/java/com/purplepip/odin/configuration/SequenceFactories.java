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

package com.purplepip.odin.configuration;

import com.purplepip.odin.creation.flow.DefaultFlowConfiguration;
import com.purplepip.odin.creation.flow.FlowConfiguration;
import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.creation.sequence.SequenceFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.music.sequence.Random;
import java.util.ArrayList;
import java.util.List;

public final class SequenceFactories {
  public static SequenceFactory<Note> newNoteSequenceFactory() {
    return newNoteSequenceFactory(new DefaultFlowConfiguration());
  }

  /**
   * Create the note sequence factory.
   *
   * @return a new note sequence factory
   */
  public static SequenceFactory<Note> newNoteSequenceFactory(FlowConfiguration flowConfiguration) {
    /*
     * Coded registration of known sequences.  In the future we may design a plugin architecture,
     * but for now it is kept tight by only allowing registered classes.
     */
    List<Class<? extends Sequence<Note>>> classes = new ArrayList<>();
    classes.add(Metronome.class);
    classes.add(Notation.class);
    classes.add(Pattern.class);
    classes.add(Random.class);
    return new SequenceFactory<>(flowConfiguration, classes);
  }

  private SequenceFactories() {
  }
}
