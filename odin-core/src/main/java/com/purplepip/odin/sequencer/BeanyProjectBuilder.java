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

import com.purplepip.odin.creation.sequence.MutableSequenceConfiguration;
import com.purplepip.odin.project.ProjectContainer;

/**
 * Project builder which generates bean based objects instead of the specialised types.  This
 * currently just impacts the sequences, for example instead of creating a DefaultNotation
 * this class creates a DefaultSequence.  The resultant behaviour should be the same, however
 * this project builder is more flexible since it allows the persistent model to be simplified
 * by only requiring one entity type and will make it easier to support plugins in the future.
 */
public class BeanyProjectBuilder extends ProjectBuilder {
  public BeanyProjectBuilder(ProjectContainer projectContainer) {
    super(projectContainer);
  }

  /**
   * Create Metronome.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return metronome
   */
  @Override
  protected MutableSequenceConfiguration createMetronome() {
    return createSequence();
  }

  /**
   * Create Notation.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return notation
   */
  @Override
  protected MutableSequenceConfiguration createNotation() {
    return createSequence();
  }

  /**
   * Create Pattern.  This method can be overridden by another sequence builder that
   * uses a different model implementation.
   *
   * @return pattern
   */
  @Override
  protected MutableSequenceConfiguration createPattern() {
    return createSequence();
  }
}
